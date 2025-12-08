package com.example.playlistmaker.data.network

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import com.example.playlistmaker.data.converters.TrackDbConverter
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.domain.api.PlaylistsRepository
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class PlaylistsRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConverter: TrackDbConverter,
    private val context: Context
) : PlaylistsRepository {

    override suspend fun createPlaylist(name: String, description: String, coverImageUri: String?) {
        withContext(Dispatchers.IO) {
            val storedImageUri = if (coverImageUri != null) {
                saveImageToPrivateStorage(Uri.parse(coverImageUri))
            } else {
                null
            }

            val playlistEntity = PlaylistEntity(
                name = name,
                description = description,
                coverImagePath = storedImageUri,
                tracksCount = 0,
                trackIds = Gson().toJson(emptyList<Long>())
            )
            appDatabase.playlistDao().insertPlaylist(playlistEntity)
        }
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return flow {
            val entities = appDatabase.playlistDao().getPlaylists()
            emit(entities.map { convertEntityToPlaylist(it) })
        }.flowOn(Dispatchers.IO)
    }

    override fun getPlaylist(playlistId: Long): Flow<Playlist?> {
        return flow {
            val entity = appDatabase.playlistDao().getPlaylistById(playlistId)
            if (entity != null) {
                val gson = Gson()
                val type = object : TypeToken<List<String>>() {}.type
                val trackIds: List<String> = gson.fromJson(entity.trackIds ?: "[]", type) ?: emptyList()

                val trackEntities = appDatabase.trackDao().getTracksByIds(trackIds)

                val tracksMap = trackEntities.associateBy { it.trackId }
                val sortedTracks = trackIds.mapNotNull { tracksMap[it] }
                    .map { trackDbConverter.map(it) }

                val playlist = Playlist(
                    id = entity.id,
                    name = entity.name,
                    description = entity.description,
                    coverImageUrl = entity.coverImagePath,
                    tracks = sortedTracks,
                    tracksCount = entity.tracksCount,
                    trackIds = entity.trackIds
                )
                emit(playlist)
            } else {
                emit(null)
            }
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        withContext(Dispatchers.IO) {
            appDatabase.trackDao().insertTrack(trackDbConverter.map(track))

            val gson = Gson()
            val currentTrackIdsType = object : TypeToken<List<String>>() {}.type
            val currentTrackIds: MutableList<String> = gson.fromJson(playlist.trackIds ?: "[]", currentTrackIdsType) ?: mutableListOf()

            if (!currentTrackIds.contains(track.trackId)) {
                currentTrackIds.add(track.trackId)

                val updatedEntity = PlaylistEntity(
                    id = playlist.id,
                    name = playlist.name,
                    description = playlist.description,
                    coverImagePath = playlist.coverImageUrl,
                    trackIds = gson.toJson(currentTrackIds),
                    tracksCount = currentTrackIds.size
                )
                appDatabase.playlistDao().updatePlaylist(updatedEntity)
            }
        }
    }

    override suspend fun deletePlaylist(playlistId: Long) {
    }

    private fun convertEntityToPlaylist(entity: PlaylistEntity): Playlist {
        return Playlist(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            coverImageUrl = entity.coverImagePath,
            tracks = emptyList(),
            tracksCount = entity.tracksCount,
            trackIds = entity.trackIds
        )
    }

    private fun saveImageToPrivateStorage(uri: Uri): String {
        val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val fileName = "cover_${System.currentTimeMillis()}.jpg"
        val file = File(filePath, fileName)

        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)
            BitmapFactory.decodeStream(inputStream).compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
            inputStream?.close()
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
        return file.absolutePath
    }
}