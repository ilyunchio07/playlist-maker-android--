package com.example.playlistmaker.creator

import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.models.Word
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class DatabaseMock(private val scope: CoroutineScope) {

    private val historyList = mutableListOf<Word>()
    private val _historyUpdates = MutableSharedFlow<Unit>()
    val historyUpdates: SharedFlow<Unit> = _historyUpdates.asSharedFlow()

    private val tracks = mutableListOf<Track>(
        Track("1", "Группа крови", "Кино", 265000, "url1"),
        Track("2", "Танцевать", "Ёлка", 222000, "url2"),
        Track("3", "Черный бумер", "Серега", 241000, "url3"),
        Track("4", "Звезда по имени Солнце", "Кино", 228000, "url4"),
        Track("5", "Вечно молодой", "Смысловые Галлюцинации", 259000, "url5"),
        Track("6", "Полковнику никто не пишет", "Би-2", 274000, "url6"),
        Track("7", "Прекрасное далёко", "Гости из Будущего", 231000, "url7"),
        Track("8", "Яндекс", "L'One", 190000, "url8"),
        Track("9", "Восьмиклассница", "Кино", 233000, "url9"),
        Track("10", "Моя бабушка курит трубку", "Гарик Сукачёв", 276000, "url10"),
        Track("11", "Blinding Lights", "The Weeknd", 200000, "url11"),
        Track("12", "Lose Yourself", "Eminem", 326000, "url12"),
        Track("13", "Bohemian Rhapsody", "Queen", 354000, "url13"),
        Track("14", "Smells Like Teen Spirit", "Nirvana", 301000, "url14"),
        Track("15", "Billie Jean", "Michael Jackson", 294000, "url15"),
        Track("16", "Shape of You", "Ed Sheeran", 233000, "url16"),
        Track("17", "Rolling in the Deep", "Adele", 228000, "url17"),
        Track("18", "Starboy", "The Weeknd", 230000, "url18"),
        Track("19", "Without Me", "Eminem", 290000, "url19"),
        Track("20", "Hotel California", "Eagles", 391000, "url20")
    )

    private val playlists = mutableListOf<Playlist>(
        Playlist(
            id = 1L,
            name = "Любимое",
            description = "Мои самые любимые треки",
            coverImageResId = R.drawable.ic_playlist_favorite
        ),
        Playlist(
            id = 2L,
            name = "Для тренировок",
            description = "Энергичная музыка",
            coverImageResId = R.drawable.ic_playlist_workout
        )
    )

    private val _dataUpdates = MutableSharedFlow<Unit>()

    fun searchTracks(expression: String): List<Track> {
        if (expression.isEmpty()) return emptyList()
        val query = expression.lowercase()
        return tracks.filter {
            it.trackName.lowercase().contains(query) ||
                    it.artistName.lowercase().contains(query)
        }
    }

    fun getTrackByNameAndArtist(track: Track): Flow<Track?> = flow {
        val found = tracks.find { it.trackName == track.trackName && it.artistName == track.artistName }
        emit(found)
    }

    fun getFavoriteTracks(): Flow<List<Track>> = flow {
        emit(tracks.filter { it.isFavorite })
        _dataUpdates.collect { emit(tracks.filter { it.isFavorite }) }
    }

    fun insertTrack(track: Track) {
        val index = tracks.indexOfFirst { it.trackId == track.trackId }
        if (index != -1) {
            tracks[index] = track
        } else {
            tracks.add(track)
        }
        notifyDataChanged()
    }

    fun deleteTracksByPlaylistId(playlistId: Long) {
        tracks.replaceAll {
            if (it.playlistId?.toLong() == playlistId) it.copy(playlistId = null) else it
        }
        notifyDataChanged()
    }

    fun getPlaylist(id: Long): Flow<Playlist?> = flow {
        val playlist = playlists.find { it.id == id }
        if (playlist != null) {
            val playlistTracks = tracks.filter { it.playlistId?.toLong() == id }
            emit(playlist.copy(tracks = playlistTracks))
        } else {
            emit(null)
        }

        _dataUpdates.collect {
            val updatedPlaylist = playlists.find { it.id == id }
            if (updatedPlaylist != null) {
                val tracks = tracks.filter { it.playlistId?.toLong() == id }
                emit(updatedPlaylist.copy(tracks = tracks))
            } else {
                emit(null)
            }
        }
    }

    fun getAllPlaylists(): Flow<List<Playlist>> = flow {
        emit(getPlaylistsWithTracks())
        _dataUpdates.collect { emit(getPlaylistsWithTracks()) }
    }

    fun addNewPlaylist(name: String, description: String, coverImageUri: String?) {
        val newId = (playlists.maxOfOrNull { it.id } ?: 0) + 1
        playlists.add(
            Playlist(
                id = newId,
                name = name,
                description = description,
                coverImageUrl = coverImageUri // Исправлено имя параметра на coverImageUrl
            )
        )
        notifyDataChanged()
    }

    fun deletePlaylistById(playlistId: Long) {
        playlists.removeIf { it.id == playlistId }
        deleteTracksByPlaylistId(playlistId)
        notifyDataChanged()
    }

    private fun getPlaylistsWithTracks(): List<Playlist> {
        return playlists.map { playlist ->
            val playlistTracks = tracks.filter { it.playlistId?.toLong() == playlist.id }
            playlist.copy(tracks = playlistTracks)
        }
    }

    fun getHistoryRequests(): List<Word> = historyList.toList()

    fun addToHistory(word: Word) {
        val existing = historyList.find { it.word == word.word }
        if (existing != null) existing.count++ else historyList.add(0, word)
        scope.launch(Dispatchers.IO) { _historyUpdates.emit(Unit) }
    }

    private fun notifyDataChanged() {
        scope.launch(Dispatchers.IO) { _dataUpdates.emit(Unit) }
    }

    fun clearHistory() {
        historyList.clear()
        scope.launch(Dispatchers.IO) { _historyUpdates.emit(Unit) }
    }


}