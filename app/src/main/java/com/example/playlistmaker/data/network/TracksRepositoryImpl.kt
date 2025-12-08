package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.converters.TrackDbConverter
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.dto.TracksSearchRequest
import com.example.playlistmaker.data.dto.TracksSearchResponse
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class TracksRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConverter: TrackDbConverter
) : TracksRepository {

    private val networkClient = RetrofitNetworkClient()

    override suspend fun searchTracks(expression: String): List<Track> = withContext(Dispatchers.IO) {
        val response = networkClient.doRequest(TracksSearchRequest(expression))

        if (response.resultCode == 200) {
            val favoriteTracksIds = appDatabase.trackDao().getTrackIds()

            (response as TracksSearchResponse).results.map { dto ->
                Track(
                    trackId = dto.trackId?.toString() ?: "0",
                    trackName = dto.trackName ?: "Unknown Track",
                    artistName = dto.artistName ?: "Unknown Artist",
                    trackTimeMillis = dto.trackTimeMillis ?: 0L,
                    artworkUrl100 = dto.artworkUrl100 ?: "",
                    isFavorite = favoriteTracksIds.contains(dto.trackId?.toString())
                )
            }
        } else {
            throw Exception("Network error")
        }
    }

    override suspend fun updateTrackFavoriteStatus(track: Track, isFavorite: Boolean) {
        val trackEntity = trackDbConverter.map(track).copy(isFavorite = isFavorite)

        if (isFavorite) {
            appDatabase.trackDao().insertTrack(trackEntity)
        } else {
            appDatabase.trackDao().deleteTrack(trackEntity)
        }
    }

    override fun getFavoriteTracks(): Flow<List<Track>> = flow {
        val tracksEntity = appDatabase.trackDao().getTracks()
        val tracks = tracksEntity.map { trackDbConverter.map(it) }
        emit(tracks)
    }.flowOn(Dispatchers.IO)

    override fun getTrackByNameAndArtist(track: Track): Flow<Track?> = flow { emit(null) }
    override suspend fun insertTrackToPlaylist(track: Track, playlistId: Long) {}
    override suspend fun deleteTrackFromPlaylist(track: Track) {}
    override suspend fun deleteTracksByPlaylistId(playlistId: Long) {}

    override suspend fun isTrackFavorite(trackId: String): Boolean {
        return withContext(Dispatchers.IO) {
            appDatabase.trackDao().isTrackFavorite(trackId)
        }
    }
}