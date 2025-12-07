package com.example.playlistmaker.domain.models

data class Playlist(
    val id: Long,
    val name: String,
    val description: String,
    val coverImageUrl: String? = null,
    val coverImageResId: Int? = null,
    var tracks: List<Track> = emptyList()
)