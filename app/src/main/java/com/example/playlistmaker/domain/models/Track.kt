package com.example.playlistmaker.domain.models

data class Track(
    val trackId: String,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val isFavorite: Boolean = false,
    val playlistId: Int? = null,
    val image: String? = null,
)