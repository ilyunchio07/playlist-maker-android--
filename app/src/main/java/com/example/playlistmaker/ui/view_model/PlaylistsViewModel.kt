package com.example.playlistmaker.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.api.PlaylistsRepository
import com.example.playlistmaker.domain.models.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistsRepository: PlaylistsRepository
) : ViewModel() {

    // ИСПРАВЛЕНИЕ 1: Было getAllPlaylists(), стало getPlaylists()
    val playlists: Flow<List<Playlist>> = playlistsRepository.getPlaylists()

    // ИСПРАВЛЕНИЕ 2: Было addNewPlaylist, стало createPlaylist
    fun createNewPlaylist(name: String, description: String, coverImageUri: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistsRepository.createPlaylist(name, description, coverImageUri)
        }
    }

    // ИСПРАВЛЕНИЕ 3: Было deletePlaylistById, стало deletePlaylist
    // (Если вы не используете удаление в UI, этот метод можно вообще убрать,
    // но чтобы починить ошибку, вот правильный вызов):
    fun deletePlaylist(playlistId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistsRepository.deletePlaylist(playlistId)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlaylistsViewModel(
                    playlistsRepository = Creator.providePlaylistsRepository()
                )
            }
        }
    }
}