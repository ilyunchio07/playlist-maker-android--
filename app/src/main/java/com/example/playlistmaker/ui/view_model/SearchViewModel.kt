package com.example.playlistmaker.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.ui.state.SearchState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SearchViewModel(
    private val tracksRepository: TracksRepository
) : ViewModel() {

    private val _searchScreenState = MutableStateFlow<SearchState>(SearchState.Initial)
    val searchScreenState: StateFlow<SearchState> = _searchScreenState.asStateFlow()

    fun search(query: String) {
        if (query.isEmpty()) return

        _searchScreenState.value = SearchState.Loading

        try {
            val tracks = tracksRepository.searchTracks(query)

            if (tracks.isNotEmpty()) {
                _searchScreenState.value = SearchState.Content(tracks)
            } else {
                _searchScreenState.value = SearchState.Empty
            }
        } catch (e: Exception) {
            _searchScreenState.value = SearchState.Error
        }
    }

    fun clearSearch() {
        _searchScreenState.value = SearchState.Initial
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel(
                    tracksRepository = Creator.provideTracksRepository()
                )
            }
        }
    }
}