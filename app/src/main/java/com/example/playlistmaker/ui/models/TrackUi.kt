package com.example.playlistmaker.ui.models

import android.os.Parcelable
import com.example.playlistmaker.domain.models.Track
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrackUi(
    val trackId: String,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val isFavorite: Boolean = false,
    val playlistId: Int? = null,
    val image: String? = null,
) : Parcelable

fun TrackUi.toDomain(): Track = Track(
    trackId = trackId,
    trackName = trackName,
    artistName = artistName,
    trackTimeMillis = trackTimeMillis,
    artworkUrl100 = artworkUrl100,
    isFavorite = isFavorite,
    playlistId = playlistId,
    image = image
)

fun Track.toUi(): TrackUi = TrackUi(
    trackId = trackId,
    trackName = trackName,
    artistName = artistName,
    trackTimeMillis = trackTimeMillis,
    artworkUrl100 = artworkUrl100,
    isFavorite = isFavorite,
    playlistId = playlistId,
    image = image
)
