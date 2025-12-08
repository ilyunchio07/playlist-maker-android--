package com.example.playlistmaker.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.api.TracksRepository
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

    private val _searchScreenState = MutableStateFlow<SearchState>(SearchState.Initial)
    val searchScreenState: StateFlow<SearchState> = _searchScreenState.asStateFlow()

    private val _historyList = MutableStateFlow<List<Word>>(emptyList())
    val historyList: StateFlow<List<Word>> = _historyList.asStateFlow()

    private var latestSearchText: String = ""
    private var searchJob: Job? = null

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            searchHistoryRepository.getHistoryRequests().collect { history ->
                _historyList.value = history
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            searchHistoryRepository.clearHistory()
            _historyList.value = emptyList()
        }
    }

    fun updateQuery(newQuery: String) {
        if (newQuery.isEmpty()) {
            _searchScreenState.value = SearchState.Initial
            // loadHistory() // Можно не вызывать, так как Flow сам обновит данные, если они изменятся
        } else {
            if (latestSearchText == newQuery) return
            latestSearchText = newQuery
            searchDebounce(newQuery)
        }
    }

    fun refreshSearch() {
        if (latestSearchText.isNotEmpty()) {
            searchRequest(latestSearchText)
        }
    }

    private fun searchDebounce(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(2000L)
            searchRequest(query)
        }
    }

    private fun searchRequest(query: String) {
        if (query.isNotEmpty()) {
            _searchScreenState.value = SearchState.Loading

            viewModelScope.launch {
                try {
                    val tracks = tracksRepository.searchTracks(query)
                    if (tracks.isEmpty()) {
                        _searchScreenState.value = SearchState.Empty
                    } else {
                        _searchScreenState.value = SearchState.Content(tracks)
                        searchHistoryRepository.addToHistory(Word(query))
                    }
                } catch (e: Exception) {
                    _searchScreenState.value = SearchState.Error
                }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel(
                    tracksRepository = Creator.provideTracksRepository(),
                    searchHistoryRepository = Creator.provideSearchHistoryRepository()
                )
            }
        }
    }
}