package com.example.playlistmaker.data.network

import com.example.playlistmaker.creator.DatabaseMock
import com.example.playlistmaker.data.dto.TracksSearchRequest
import com.example.playlistmaker.data.dto.TracksSearchResponse
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class TracksRepositoryImpl(
    private val database: DatabaseMock
) : TracksRepository {

    private val networkClient = RetrofitNetworkClient()

    override suspend fun searchTracks(expression: String): List<Track> = withContext(Dispatchers.IO) {
        val response = networkClient.doRequest(TracksSearchRequest(expression))

        if (response.resultCode == 200) {
            (response as TracksSearchResponse).results.map { dto ->
                Track(
                    trackId = dto.trackId?.toString() ?: "0",
                    trackName = dto.trackName ?: "Unknown Track",
                    artistName = dto.artistName ?: "Unknown Artist",
                    trackTimeMillis = dto.trackTimeMillis ?: 0L,
                    artworkUrl100 = dto.artworkUrl100 ?: "",
                    isFavorite = false,
                    playlistId = null,
                    image = dto.artworkUrl100 ?: ""
                )
            }
        } else {
            throw Exception("Network error or bad request: ${response.resultCode}")
        }
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