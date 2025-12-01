package com.example.playlistmaker.data.network

import com.example.playlistmaker.creator.DatabaseMock
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

class TracksRepositoryImpl(
    private val database: DatabaseMock
) : TracksRepository {

    override suspend fun searchTracks(expression: String): List<Track> {
        return database.searchTracks(expression)
    }

    override fun getTrackByNameAndArtist(track: Track): Flow<Track?> {
        return database.getTrackByNameAndArtist(track)
    }

    override suspend fun insertTrackToPlaylist(track: Track, playlistId: Long) {
        database.insertTrack(track.copy(playlistId = playlistId.toInt()))
    }

    override suspend fun deleteTrackFromPlaylist(track: Track) {
        database.insertTrack(track.copy(playlistId = null))
    }

    override suspend fun updateTrackFavoriteStatus(track: Track, isFavorite: Boolean) {
        database.insertTrack(track.copy(isFavorite = isFavorite))
    }

    override suspend fun deleteTracksByPlaylistId(playlistId: Long) {
        database.deleteTracksByPlaylistId(playlistId)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return database.getFavoriteTracks()
    }
}