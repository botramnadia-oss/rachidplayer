package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stream_history")
data class StreamHistoryItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val url: String,
    val streamType: String, // "Audio Stream", "MP4 Video", "Live Radio AAC", "HLS Live"
    val title: String,
    val timeAgo: String = "Just now",
    val timestamp: Long = System.currentTimeMillis()
)
