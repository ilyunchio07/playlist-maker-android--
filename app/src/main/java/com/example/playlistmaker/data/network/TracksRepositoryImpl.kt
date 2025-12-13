package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.converters.TrackDbConverter
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.dto.TracksSearchRequest
import com.example.playlistmaker.data.dto.TracksSearchResponse
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.Resource
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val appDatabase: AppDatabase,
    private val trackDbConverter: TrackDbConverter
) : TracksRepository {

    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TracksSearchRequest(expression))

        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error("Проверьте подключение к интернету"))
            }
            200 -> {
                val tracks = (response as TracksSearchResponse).results.map { dto ->
                    Track(
                        trackId = dto.trackId?.toString() ?: "",
                        trackName = dto.trackName ?: "Unknown Track",
                        artistName = dto.artistName ?: "Unknown Artist",
                        trackTimeMillis = dto.trackTimeMillis ?: 0L,
                        artworkUrl100 = dto.artworkUrl100 ?: "",
                        isFavorite = false
                    )
                }
                emit(Resource.Success(tracks))
            }
            else -> {
                emit(Resource.Error("Ошибка сервера"))
            }
        }
    }.flowOn(Dispatchers.IO)

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return appDatabase.trackDao().getFavoriteTracks()
            .map { entities ->
                entities.map { trackEntity -> trackDbConverter.map(trackEntity) }
            }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun isTrackFavorite(trackId: String): Boolean {
        return appDatabase.trackDao().isFavorite(trackId)
    }

    override suspend fun updateTrackFavoriteStatus(track: Track, isFavorite: Boolean) {
        val trackEntity = trackDbConverter.map(track.copy(isFavorite = isFavorite))
        appDatabase.trackDao().insertTrack(trackEntity)
    }

    override fun getTrackByNameAndArtist(track: Track): Flow<Track?> = flow { emit(null) }
    override suspend fun insertTrackToPlaylist(track: Track, playlistId: Long) {
        val trackEntity = trackDbConverter.map(track)
        appDatabase.trackDao().insertTrack(trackEntity)

        val playlistTrackEntity = com.example.playlistmaker.data.db.entity.PlaylistTrackEntity(
            playlistId = playlistId,
            trackId = track.trackId
        )
        appDatabase.playlistDao().insertPlaylistTrack(playlistTrackEntity)

        val playlist = appDatabase.playlistDao().getPlaylistById(playlistId)

        if (playlist != null) {
            val updatedPlaylist = playlist.copy(
                tracksCount = playlist.tracksCount + 1
            )
            appDatabase.playlistDao().updatePlaylist(updatedPlaylist)
        }
    }

    override suspend fun deleteTrackFromPlaylist(track: Track) {}
    override suspend fun deleteTracksByPlaylistId(playlistId: Long) {}
}
