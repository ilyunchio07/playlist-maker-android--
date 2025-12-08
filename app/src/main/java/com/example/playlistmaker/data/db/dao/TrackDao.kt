package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.entity.TrackEntity

@Dao
interface TrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    @Delete
    suspend fun deleteTrack(track: TrackEntity)

    @Query("SELECT * FROM track_table WHERE isFavorite = 1 ORDER BY addedTimestamp DESC")
    suspend fun getTracks(): List<TrackEntity>

    @Query("SELECT trackId FROM track_table WHERE isFavorite = 1")
    suspend fun getTrackIds(): List<String>

    @Query("SELECT * FROM track_table WHERE trackId IN (:ids)")
    suspend fun getTracksByIds(ids: List<String>): List<TrackEntity>

    @Query("SELECT EXISTS (SELECT 1 FROM track_table WHERE trackId = :trackId AND isFavorite = 1)")
    suspend fun isTrackFavorite(trackId: String): Boolean
}