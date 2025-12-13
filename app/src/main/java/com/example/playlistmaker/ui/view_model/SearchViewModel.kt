package com.example.playlistmaker.ui.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.data.network.SearchHistoryRepositoryImpl
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.Resource
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.models.Word
import com.example.playlistmaker.ui.state.SearchState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val tracksRepository: TracksRepository,
    private val searchHistoryRepository: SearchHistoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow<SearchState>(SearchState.Initial)
    val state: StateFlow<SearchState> = _state.asStateFlow()

    private val _historyList = MutableStateFlow<List<Word>>(emptyList())
    val historyList: StateFlow<List<Word>> = _historyList.asStateFlow()

    private var latestSearchText: String = ""
    private var searchJob: Job? = null

    init {
        viewModelScope.launch {
            searchHistoryRepository.getHistoryRequests()
                .collect { history ->
                    _historyList.value = history
                }
        }
    }

    fun updateQuery(newQuery: String, immediate: Boolean = false) {
        if (!immediate && latestSearchText == newQuery) return

        latestSearchText = newQuery
        searchJob?.cancel()

        if (newQuery.isNotEmpty()) {
            searchJob = viewModelScope.launch {
                if (!immediate) {
                    delay(SEARCH_DEBOUNCE_DELAY)
                }
                performSearch(newQuery)
            }
        } else {
            renderState(SearchState.Initial)
        }
    }

    fun refreshSearch() {
        if (latestSearchText.isNotEmpty()) {
            performSearch(latestSearchText)
        }
    }

    private fun performSearch(query: String) {
        renderState(SearchState.Loading)

        viewModelScope.launch {
            tracksRepository
                .searchTracks(query)
                .collect { result ->
                    when(result) {
                        is Resource.Success -> {
                            val tracks = result.data
                            searchHistoryRepository.addToHistory(Word(query))
                            if (tracks.isNullOrEmpty()) {
                                renderState(SearchState.Empty)
                            } else {
                                renderState(SearchState.Content(tracks))
                            }
                        }
                        is Resource.Error -> {
                            renderState(SearchState.Error(result.message))
                        }
                    }
                }
        }
    }

    private fun renderState(state: SearchState) {
        _state.value = state
    }

    fun addToHistory(track: Track) {
        viewModelScope.launch {
            searchHistoryRepository.addToHistory(Word(track.trackName))
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            searchHistoryRepository.clearHistory()
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application)

                val searchHistoryPrefs = Creator.provideSearchHistoryPreferences(application)

                val searchHistoryRepository = SearchHistoryRepositoryImpl(searchHistoryPrefs)

                SearchViewModel(
                    tracksRepository = Creator.provideTracksRepository(application),
                    searchHistoryRepository = searchHistoryRepository
                )
            }
        }
    }
}
