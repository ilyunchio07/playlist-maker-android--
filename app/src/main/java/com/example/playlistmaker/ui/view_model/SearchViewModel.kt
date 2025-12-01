package com.example.playlistmaker.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.data.network.SearchHistoryRepositoryImpl
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.Word
import com.example.playlistmaker.ui.state.SearchState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class SearchViewModel(
    private val tracksRepository: TracksRepository
) : ViewModel() {

    private val searchHistoryRepository = SearchHistoryRepositoryImpl(scope = viewModelScope)

    private val _searchScreenState = MutableStateFlow<SearchState>(SearchState.Initial)
    val searchScreenState: StateFlow<SearchState> = _searchScreenState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _historyList = MutableStateFlow<List<Word>>(emptyList())
    val historyList: StateFlow<List<Word>> = _historyList.asStateFlow()

    init {
        viewModelScope.launch {
            searchHistoryRepository.getHistoryRequests().collect { history ->
                _historyList.value = history
            }
        }

        _searchQuery
            .debounce(1000L)
            .distinctUntilChanged()
            .filter { it.isNotEmpty() }
            .onEach { query ->
                performSearch(query)
            }
            .launchIn(viewModelScope)
    }

    fun updateQuery(newQuery: String) {
        _searchQuery.value = newQuery
        if (newQuery.isEmpty()) {
            _searchScreenState.value = SearchState.Initial
        }
    }

    private fun performSearch(query: String) {
        _searchScreenState.value = SearchState.Loading

        viewModelScope.launch {
            searchHistoryRepository.addToHistory(Word(query))

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