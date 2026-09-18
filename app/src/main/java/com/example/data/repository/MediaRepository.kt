package com.example.data.repository

import com.example.data.local.MediaDao
import com.example.data.local.StreamHistoryDao
import com.example.data.model.MediaItem
import com.example.data.model.StreamHistoryItem
import kotlinx.coroutines.flow.Flow

class MediaRepository(
    private val mediaDao: MediaDao,
    private val streamHistoryDao: StreamHistoryDao
) {
    val allMedia: Flow<List<MediaItem>> = mediaDao.getAllMedia()
    val allHistory: Flow<List<StreamHistoryItem>> = streamHistoryDao.getAllHistory()

    fun getMediaByType(type: String): Flow<List<MediaItem>> = mediaDao.getMediaByType(type)
    fun searchMedia(query: String): Flow<List<MediaItem>> = mediaDao.searchMedia(query)

    suspend fun toggleFavorite(mediaItem: MediaItem) {
        mediaDao.updateMedia(mediaItem.copy(isFavorite = !mediaItem.isFavorite))
    }

    suspend fun deleteMedia(mediaItem: MediaItem) {
        mediaDao.deleteMedia(mediaItem)
    }

    suspend fun addStreamHistory(url: String, title: String, type: String) {
        streamHistoryDao.insertHistory(
            StreamHistoryItem(
                url = url,
                title = title,
                streamType = type,
                timeAgo = "Just now",
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun deleteStreamHistory(id: Long) {
        streamHistoryDao.deleteHistoryById(id)
    }

    suspend fun clearStreamHistory() {
        streamHistoryDao.clearHistory()
    }

    suspend fun resetDatabase() {
        mediaDao.deleteAll()
        streamHistoryDao.clearHistory()
        seedInitialData()
    }

    suspend fun seedInitialData() {
        if (mediaDao.getCount() == 0) {
            val defaultMedia = listOf(
                MediaItem(
                    title = "Midnight Echoes.flac",
                    artist = "The Synthetics",
                    album = "Neon Horizon (2024)",
                    type = "AUDIO",
                    formatBadge = "Lossless FLAC",
                    filePath = "/storage/emulated/0/Music/Midnight Echoes.flac",
                    fileSize = "38.2 MB",
                    fileSizeBytes = 38_200_000L,
                    durationSeconds = 268L,
                    formattedDuration = "4:28",
                    audioStreamSpec = "FLAC 24-bit / 96kHz Lossless",
                    folder = "Music",
                    artworkUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDxSMLBHLee2T_QrVXSWFFL2uN93TTONacvgF2__MhPxyiWVgK2jrEEsVFSnZ1Z4RStc0yxd3xvsgW5SbnutJdfYg94J4hNQVrTzXEgdZtUxqRxrIUof0CSyxDKbK7Q-TxQ8QoSv2LsSOSqBz8JP_icLq-1bs0V-89t7XIkgFwakw6r3H6fFnSjYfNZRd66f_Z__cCTPYt7tQNahD4benH2eHZk4GRa30JU1RVUEKwMKNQUZaS1Qq9asQ"
                ),
                MediaItem(
                    title = "Cinematic_Drone_4K.mp4",
                    artist = "Camera / DCIM",
                    album = "Drone Captures",
                    type = "VIDEO",
                    formatBadge = "4K 60fps",
                    filePath = "/storage/emulated/0/DCIM/Camera/Cinematic_Drone_4K.mp4",
                    fileSize = "420 MB",
                    fileSizeBytes = 420_000_000L,
                    durationSeconds = 765L,
                    formattedDuration = "12:45",
                    videoResolution = "3840x2160",
                    videoFps = 60,
                    videoCodec = "H.265 (HEVC)",
                    audioStreamSpec = "AAC 48kHz Stereo",
                    folder = "DCIM",
                    artworkUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAU7tZ9uo4f6OcoPf1q8vCRA8HeS9rGH0b2E1OTFbQC-9ZWn5AqcA8leP0VriUlKeIE6ltx7Vx68e8P43arpcnKve68t6Dp5G6DhWcKH2qn2THj_kMyYnhSBNHwbh1w_e_OliCQmLSpAA_XPaqKUCRSGpRX6VKnRwArMslPTSet84Ggq2gMlbSj4Sw3bVqvzTQkovn26Y9iIggKm4VWURTe5ykRK6osOQGjGeGtKRjX6vIl-ZXIdDm6Hg"
                ),
                MediaItem(
                    title = "Deep_Focus_Session.mp3",
                    artist = "Ambient Soundscapes",
                    album = "Mindful Space",
                    type = "AUDIO",
                    formatBadge = "320kbps MP3",
                    filePath = "/storage/emulated/0/Music/Deep_Focus_Session.mp3",
                    fileSize = "104 MB",
                    fileSizeBytes = 104_000_000L,
                    durationSeconds = 2710L,
                    formattedDuration = "45:10",
                    audioStreamSpec = "320kbps MP3",
                    folder = "Music",
                    artworkUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuClaMo5pJzMMGoSDeLZC9J3NXfBlM2yzzslpqBiIaeVhKqQ0LlzUMWZxDcl1UWOeYw2K-EMWmg1SgEq_olxbloAe1OtyMUo4_zOaNrbh9IJLIIgGk9kTSO1iv6G_lK6YB2VHus2zpKALIh3-hRFzcGzMCk-z7LFMxXmlPZy7vKSrdGCvVVfoDfFG2qitVbqw7ZOpgx_lNnxWGmQSwiS7gIr7o3ha2CcPlP8DT84xAiekXKK9UufvE9lhg"
                ),
                MediaItem(
                    title = "Tutorial_Kotlin_Coroutines.mkv",
                    artist = "Downloads",
                    album = "Developer Tutorials",
                    type = "VIDEO",
                    formatBadge = "1080p MKV",
                    filePath = "/storage/emulated/0/Download/Tutorial_Kotlin_Coroutines.mkv",
                    fileSize = "310 MB",
                    fileSizeBytes = 310_000_000L,
                    durationSeconds = 1695L,
                    formattedDuration = "28:15",
                    videoResolution = "1920x1080",
                    videoFps = 30,
                    videoCodec = "H.264 (AVC)",
                    audioStreamSpec = "AAC 44.1kHz Stereo",
                    folder = "Downloads",
                    artworkUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAPt8LnmDbOLE-XaelUYc6sKmP_63kUYkt2yTfax02Hf5CRuPwbW49ZPISD_5SGUNENRzlLFB8PsFgY3JqfpmJ3wJMqjBhA54BG-hE9Pr6VhSllvrRynBdzioU3LVS731bFYzl8bUJJMN9LBgsHwYlk3uS5ZiNm6vHYVfHg07HQL1HzrbcWjL-RRiPJgrlPeyXgNMd32r_wKFCCdrU2tBVJh8QvsIWZpz8VCCznAFo-ntpVbSGIiqbLTQ"
                ),
                MediaItem(
                    title = "Acoustic_Soul_Session.wav",
                    artist = "Studio Master",
                    album = "Unplugged Live",
                    type = "AUDIO",
                    formatBadge = "24-bit WAV",
                    filePath = "/storage/emulated/0/Music/Acoustic_Soul_Session.wav",
                    fileSize = "52 MB",
                    fileSizeBytes = 52_000_000L,
                    durationSeconds = 232L,
                    formattedDuration = "3:52",
                    audioStreamSpec = "24-bit WAV Studio Master",
                    folder = "Music",
                    artworkUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAFbf4bteTr7plIT_iLPSNkeMt9nptE0gXkG9lRWF59t6hsqg1JtGeUgSSChwMFA4hG531-0CIHAezCGQ21WOa0Ori_p_lr3_ePvPVC7r9yCd-8_Gi0Ba303GStyfdRSwuHvo0mvPuG7yPMN2US0MnnLlCKSIuujKHKOw2y3H2NXCMdX2EK8DOYueChPPAvrPy2La7NqvgEoyrgnmvyY-bANQrvcmhU7aJKciTlYLjkqft4zCGcLG1poQ"
                ),
                MediaItem(
                    title = "Mountain_Summit_Sunrise.mp4",
                    artist = "Camera / DCIM",
                    album = "Nature Clips",
                    type = "VIDEO",
                    formatBadge = "1080p 60FPS",
                    filePath = "/storage/emulated/0/DCIM/Camera/Mountain_Summit_Sunrise.mp4",
                    fileSize = "245 MB",
                    fileSizeBytes = 245_000_000L,
                    durationSeconds = 504L,
                    formattedDuration = "08:24",
                    videoResolution = "1920x1080",
                    videoFps = 60,
                    videoCodec = "H.264 (AVC)",
                    audioStreamSpec = "AAC 48kHz Stereo",
                    folder = "DCIM",
                    artworkUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCJhL5Xul8jCHqGTvQqXASaUcIEdmLFDKvEizlnLwr2rmuqPLVzFLlsON3-crvs71ofNYHrlF09rMfxj2Sjz_kHZntoIB853hlTRwkF-H3YM0r4-e5ZTwkaTya1FXL5XtCrQkmQJlsLdoKAJl1kGlw-QxQ_9b69aDHva18lu6Z5cYT0abxPAkZe794_gK2rM8-6tzzhXrXwfoyAZYvJWJN2zV7g3jowawc-i-tJtTnVQyW7gm1t0SkrAQ"
                ),
                MediaItem(
                    title = "Cyberpunk_Neon_City_Night.mov",
                    artist = "Camera / DCIM",
                    album = "Night City",
                    type = "VIDEO",
                    formatBadge = "4K UHD",
                    filePath = "/storage/emulated/0/DCIM/Camera/Cyberpunk_Neon_City_Night.mov",
                    fileSize = "810 MB",
                    fileSizeBytes = 810_000_000L,
                    durationSeconds = 950L,
                    formattedDuration = "15:50",
                    videoResolution = "3840x2160",
                    videoFps = 30,
                    videoCodec = "ProRes / H.265",
                    audioStreamSpec = "PCM Stereo",
                    folder = "DCIM",
                    artworkUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuD5eT-xF0K1kvqes-CyetLno6vLAeYChzTmGGNU6S021O2Td7ryGBZdITgbKboXR7MEL4TVdGLpd8B4UOW0JPosX3xnXxBoLQIBDkJBcYZHeZA0tzOLfa_lbxPpAlm1_6ajLyJzP0eHjDsjZISTMkOeD5NhoxTC_AhZKianySS90tSLIX0w2pfcR4HI-fU46ZWtNOV9ipVflBBcJ665Duv8EaUVz_BSjhJ6iibUFwjrGBq3ZcbySu0Sug"
                ),
                MediaItem(
                    title = "FPV_Forest_Speed_Run.mp4",
                    artist = "Downloads",
                    album = "Drone Racing",
                    type = "VIDEO",
                    formatBadge = "1440p 120FPS",
                    filePath = "/storage/emulated/0/Download/FPV_Forest_Speed_Run.mp4",
                    fileSize = "190 MB",
                    fileSizeBytes = 190_000_000L,
                    durationSeconds = 192L,
                    formattedDuration = "03:12",
                    videoResolution = "2560x1440",
                    videoFps = 120,
                    videoCodec = "H.265 (HEVC)",
                    audioStreamSpec = "AAC 48kHz Stereo",
                    folder = "Downloads",
                    artworkUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDHT-AUP2kkXWisdb8iunhF5Xko22I_gL_KMS1bL2SSb089FeZbfhDKDzhznoV0bUQ2xFKXIHcrOpnZBVXHiKbBnzjO6nxdl_QLO_A2pZd7qCvdUPFtnyoglybzDT5NElMpfSf5Zqk9l-8giGRPRxv6xKRm255cBONi8pkHIwSkdaMTrgsVpDmH6E5ABXsAMAqGzWOdLELIqQwc9HXaVgaHSPEsWyKzvqWnyrF2P53VbAhFctPnjWCHLw"
                )
            )
            mediaDao.insertMediaList(defaultMedia)

            val defaultStreams = listOf(
                StreamHistoryItem(
                    url = "https://cdn.example.org/live/podcast_ep42.mp3",
                    title = "podcast_ep42.mp3",
                    streamType = "Audio Stream",
                    timeAgo = "Yesterday",
                    timestamp = System.currentTimeMillis() - 86400000L
                ),
                StreamHistoryItem(
                    url = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                    title = "BigBuckBunny.mp4",
                    streamType = "MP4 Video",
                    timeAgo = "3 days ago",
                    timestamp = System.currentTimeMillis() - 3 * 86400000L
                ),
                StreamHistoryItem(
                    url = "http://streaming.radio/jazz-direct.aac",
                    title = "jazz-direct.aac",
                    streamType = "Live Radio AAC",
                    timeAgo = "5 days ago",
                    timestamp = System.currentTimeMillis() - 5 * 86400000L
                )
            )
            defaultStreams.forEach { streamHistoryDao.insertHistory(it) }
        }
    }
}
