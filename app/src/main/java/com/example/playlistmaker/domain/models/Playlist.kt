package com.example.playlistmaker.domain.models

data class Playlist(
    val id: Long,
    val name: String,
    val description: String,
    var tracks: List<Track> = emptyList()
)