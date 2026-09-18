package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "media_items")
data class MediaItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val artist: String,
    val album: String = "",
    val type: String, // "AUDIO" or "VIDEO"
    val formatBadge: String, // e.g. "Lossless FLAC", "4K 60fps", "320kbps MP3"
    val filePath: String,
    val fileSize: String,
    val fileSizeBytes: Long = 0L,
    val durationSeconds: Long,
    val formattedDuration: String,
    val videoResolution: String = "",
    val videoFps: Int = 0,
    val videoCodec: String = "",
    val audioStreamSpec: String = "",
    val isFavorite: Boolean = false,
    val dateAdded: Long = System.currentTimeMillis(),
    val folder: String = "Music",
    val artworkUrl: String = ""
)
