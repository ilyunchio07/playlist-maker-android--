package com.example.playlistmaker.ui.state

import com.example.playlistmaker.domain.models.Track

sealed interface SearchState {
    data object Initial : SearchState
    data object Default : SearchState

    data object Loading : SearchState

    data object Empty : SearchState

    data class Content(val tracks: List<Track>) : SearchState

    data class Error(val message: String) : SearchState
}
