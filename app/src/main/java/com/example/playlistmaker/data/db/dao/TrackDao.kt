package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    @Query("SELECT * FROM track_table WHERE isFavorite = 1")
    fun getFavoriteTracks(): Flow<List<TrackEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM track_table WHERE trackId = :trackId AND isFavorite = 1)")
    suspend fun isFavorite(trackId: String): Boolean

    @Query("SELECT * FROM track_table WHERE trackId IN (:ids) ORDER BY trackTimeMillis DESC")
    suspend fun getTracksByIds(ids: List<String>): List<TrackEntity>
}
