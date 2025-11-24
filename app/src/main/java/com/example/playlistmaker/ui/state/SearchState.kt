package com.example.playlistmaker.ui.state

import com.example.playlistmaker.domain.models.Track

sealed interface SearchState {
    data object Initial : SearchState
    data object Loading : SearchState
    data class Content(val tracks: List<Track>) : SearchState
    data object Error : SearchState
    data object Empty : SearchState
}