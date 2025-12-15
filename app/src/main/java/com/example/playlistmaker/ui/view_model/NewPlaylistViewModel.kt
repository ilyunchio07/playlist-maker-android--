package com.example.playlistmaker.ui.view_model

import android.net.Uri
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.api.PlaylistsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NewPlaylistViewModel(
    private val playlistsRepository: PlaylistsRepository
) : ViewModel() {

    private val _playlistName = MutableStateFlow("")
    val playlistName = _playlistName.asStateFlow()

    private val _playlistDescription = MutableStateFlow("")
    val playlistDescription = _playlistDescription.asStateFlow()

    private val _coverUri = MutableStateFlow<Uri?>(null)
    val coverUri = _coverUri.asStateFlow()

    fun onNameChanged(text: String) {
        _playlistName.value = text
    }

    fun onDescriptionChanged(text: String) {
        _playlistDescription.value = text
    }

    fun onImageSelected(uri: Uri?) {
        _coverUri.value = uri
    }

    fun createPlaylist(onSuccess: () -> Unit) {
        val name = _playlistName.value
        val description = _playlistDescription.value
        val uri = _coverUri.value

        if (name.isNotEmpty()) {
            viewModelScope.launch {
                playlistsRepository.createPlaylist(name, description, uri?.toString())
                onSuccess()
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application)

                NewPlaylistViewModel(
                    playlistsRepository = Creator.providePlaylistsRepository(application)
                )
            }
        }
    }
}
