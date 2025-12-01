package com.example.playlistmaker.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.api.PlaylistsRepository
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TrackDetailsViewModel(
    private val tracksRepository: TracksRepository,
    private val playlistsRepository: PlaylistsRepository
) : ViewModel() {

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    val playlists: Flow<List<Playlist>> = playlistsRepository.getAllPlaylists()

    fun checkFavoriteStatus(track: Track) {
        _isFavorite.value = track.isFavorite
    }

    fun onFavoriteClicked(track: Track) {
        viewModelScope.launch(Dispatchers.IO) {
            val newStatus = !_isFavorite.value
            tracksRepository.updateTrackFavoriteStatus(track, newStatus)
            _isFavorite.value = newStatus
        }
    }

    fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        viewModelScope.launch(Dispatchers.IO) {
            tracksRepository.insertTrackToPlaylist(track, playlist.id)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                TrackDetailsViewModel(
                    tracksRepository = Creator.provideTracksRepository(),
                    playlistsRepository = Creator.providePlaylistsRepository()
                )
            }
        }
    }
}