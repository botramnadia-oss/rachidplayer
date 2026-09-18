package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.StreamHistoryItem
import kotlinx.coroutines.flow.Flow

@Dao
interface StreamHistoryDao {
    @Query("SELECT * FROM stream_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<StreamHistoryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(item: StreamHistoryItem): Long

    @Query("DELETE FROM stream_history WHERE id = :id")
    suspend fun deleteHistoryById(id: Long)

    @Query("DELETE FROM stream_history")
    suspend fun clearHistory()
}
