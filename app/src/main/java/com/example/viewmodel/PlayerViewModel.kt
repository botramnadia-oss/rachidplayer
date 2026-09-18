package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.MediaItem
import com.example.data.model.StreamHistoryItem
import com.example.data.repository.MediaRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ScreenTab {
    LIBRARY, VIDEO, STREAM, SETTINGS
}

class PlayerViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: MediaRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = MediaRepository(db.mediaDao(), db.streamHistoryDao())
        viewModelScope.launch {
            repository.seedInitialData()
        }
    }

    // Navigation state
    private val _currentTab = MutableStateFlow(ScreenTab.LIBRARY)
    val currentTab: StateFlow<ScreenTab> = _currentTab.asStateFlow()

    private val _isNowPlayingExpanded = MutableStateFlow(false)
    val isNowPlayingExpanded: StateFlow<Boolean> = _isNowPlayingExpanded.asStateFlow()

    fun setTab(tab: ScreenTab) {
        _currentTab.value = tab
    }

    fun setNowPlayingExpanded(expanded: Boolean) {
        _isNowPlayingExpanded.value = expanded
    }

    // Media and search state
    val allMedia: StateFlow<List<MediaItem>> = repository.allMedia.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val allHistory: StateFlow<List<StreamHistoryItem>> = repository.allHistory.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFilter = MutableStateFlow("ALL") // ALL, MUSIC, VIDEOS, FOLDERS, RECENT
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    private val _sortOption = MutableStateFlow("Date Added") // "Date Added", "Title", "Size", "Duration"
    val sortOption: StateFlow<String> = _sortOption.asStateFlow()

    val filteredMedia: StateFlow<List<MediaItem>> = combine(
        allMedia,
        _searchQuery,
        _selectedFilter,
        _sortOption
    ) { mediaList, query, filter, sort ->
        var list = mediaList

        // Filter by tab / category
        list = when (filter) {
            "MUSIC" -> list.filter { it.type == "AUDIO" }
            "VIDEOS" -> list.filter { it.type == "VIDEO" }
            "FOLDERS" -> list.sortedBy { it.folder }
            "RECENT" -> list.sortedByDescending { it.dateAdded }
            else -> list
        }

        // Filter by search query
        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            list = list.filter {
                it.title.lowercase().contains(q) ||
                it.artist.lowercase().contains(q) ||
                it.folder.lowercase().contains(q) ||
                it.album.lowercase().contains(q)
            }
        }

        // Sort
        when (sort) {
            "Title" -> list.sortedBy { it.title.lowercase() }
            "Size" -> list.sortedByDescending { it.fileSizeBytes }
            "Duration" -> list.sortedByDescending { it.durationSeconds }
            else -> list.sortedByDescending { it.dateAdded }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilter(filter: String) {
        _selectedFilter.value = filter
    }

    fun setSortOption(sort: String) {
        _sortOption.value = sort
    }

    // Playback state
    private val _currentTrack = MutableStateFlow<MediaItem?>(null)
    val currentTrack: StateFlow<MediaItem?> = _currentTrack.asStateFlow()

    private val _isPlaying = MutableStateFlow(true)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(108_000L) // 01:48 default
    val currentPositionMs: StateFlow<Long> = _currentPositionMs.asStateFlow()

    private val _durationMs = MutableStateFlow(268_000L) // 04:28 default
    val durationMs: StateFlow<Long> = _durationMs.asStateFlow()

    private val _repeatMode = MutableStateFlow(1) // 0: Off, 1: Repeat All, 2: Repeat One
    val repeatMode: StateFlow<Int> = _repeatMode.asStateFlow()

    private val _isShuffle = MutableStateFlow(false)
    val isShuffle: StateFlow<Boolean> = _isShuffle.asStateFlow()

    private val _playbackSpeed = MutableStateFlow(1.0f)
    val playbackSpeed: StateFlow<Float> = _playbackSpeed.asStateFlow()

    private val _volume = MutableStateFlow(0.75f)
    val volume: StateFlow<Float> = _volume.asStateFlow()

    private val _isMuted = MutableStateFlow(false)
    val isMuted: StateFlow<Boolean> = _isMuted.asStateFlow()

    // Video Player state
    private val _currentVideo = MutableStateFlow<MediaItem?>(null)
    val currentVideo: StateFlow<MediaItem?> = _currentVideo.asStateFlow()

    private val _videoPositionMs = MutableStateFlow(255_000L) // 04:15
    val videoPositionMs: StateFlow<Long> = _videoPositionMs.asStateFlow()

    private val _videoDurationMs = MutableStateFlow(765_000L) // 12:45
    val videoDurationMs: StateFlow<Long> = _videoDurationMs.asStateFlow()

    private val _videoPlaying = MutableStateFlow(true)
    val videoPlaying: StateFlow<Boolean> = _videoPlaying.asStateFlow()

    private val _videoAspectRatio = MutableStateFlow("16:9")
    val videoAspectRatio: StateFlow<String> = _videoAspectRatio.asStateFlow()

    private val _videoSpeed = MutableStateFlow(1.25f)
    val videoSpeed: StateFlow<Float> = _videoSpeed.asStateFlow()

    private val _isSubtitlesOn = MutableStateFlow(true)
    val isSubtitlesOn: StateFlow<Boolean> = _isSubtitlesOn.asStateFlow()

    private val _isControlsLocked = MutableStateFlow(false)
    val isControlsLocked: StateFlow<Boolean> = _isControlsLocked.asStateFlow()

    private val _videoAudioTrack = MutableStateFlow("Track 1: Stereo")
    val videoAudioTrack: StateFlow<String> = _videoAudioTrack.asStateFlow()

    private val _brightnessLevel = MutableStateFlow(0.85f)
    val brightnessLevel: StateFlow<Float> = _brightnessLevel.asStateFlow()

    private val _isBackgroundAudio = MutableStateFlow(false)
    val isBackgroundAudio: StateFlow<Boolean> = _isBackgroundAudio.asStateFlow()

    // Sleep timer
    private val _sleepTimerActive = MutableStateFlow(true)
    val sleepTimerActive: StateFlow<Boolean> = _sleepTimerActive.asStateFlow()

    private val _sleepTimerMinutes = MutableStateFlow(30)
    val sleepTimerMinutes: StateFlow<Int> = _sleepTimerMinutes.asStateFlow()

    private val _sleepTimerSecondsRemaining = MutableStateFlow(1800L)
    val sleepTimerSecondsRemaining: StateFlow<Long> = _sleepTimerSecondsRemaining.asStateFlow()

    private val _sleepTimerWaitForTrackEnd = MutableStateFlow(true)
    val sleepTimerWaitForTrackEnd: StateFlow<Boolean> = _sleepTimerWaitForTrackEnd.asStateFlow()

    // Scanner
    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _lastScanText = MutableStateFlow("Last scan: Just now • 248 items found")
    val lastScanText: StateFlow<String> = _lastScanText.asStateFlow()

    // Stream inputs
    private val _streamUrlInput = MutableStateFlow("https://cdn.livebroadcast.network/audio/master.m3u8")
    val streamUrlInput: StateFlow<String> = _streamUrlInput.asStateFlow()

    private val _preloadBuffer = MutableStateFlow(true)
    val preloadBuffer: StateFlow<Boolean> = _preloadBuffer.asStateFlow()

    // Settings
    private val _autoScanOnLaunch = MutableStateFlow(true)
    val autoScanOnLaunch: StateFlow<Boolean> = _autoScanOnLaunch.asStateFlow()

    private val _includeHiddenFolders = MutableStateFlow(false)
    val includeHiddenFolders: StateFlow<Boolean> = _includeHiddenFolders.asStateFlow()

    private val _targetedFolders = MutableStateFlow("/storage/emulated/0/Music, /DCIM")
    val targetedFolders: StateFlow<String> = _targetedFolders.asStateFlow()

    private val _audioEngine = MutableStateFlow("Audio Engine: OpenSL ES / ExoPlayer Native")
    val audioEngine: StateFlow<String> = _audioEngine.asStateFlow()

    private val _resumePlayback = MutableStateFlow(true)
    val resumePlayback: StateFlow<Boolean> = _resumePlayback.asStateFlow()

    private val _rememberSpeed = MutableStateFlow(true)
    val rememberSpeed: StateFlow<Boolean> = _rememberSpeed.asStateFlow()

    private val _hardwareAccel = MutableStateFlow(true)
    val hardwareAccel: StateFlow<Boolean> = _hardwareAccel.asStateFlow()

    private var playbackJob: Job? = null
    private var sleepTimerJob: Job? = null

    init {
        viewModelScope.launch {
            allMedia.collect { list ->
                if (_currentTrack.value == null && list.isNotEmpty()) {
                    val defaultTrack = list.find { it.title.startsWith("Midnight") } ?: list.first()
                    _currentTrack.value = defaultTrack
                    _durationMs.value = defaultTrack.durationSeconds * 1000L
                }
                if (_currentVideo.value == null && list.isNotEmpty()) {
                    val defaultVideo = list.find { it.type == "VIDEO" }
                    if (defaultVideo != null) {
                        _currentVideo.value = defaultVideo
                        _videoDurationMs.value = defaultVideo.durationSeconds * 1000L
                    }
                }
            }
        }
        startPlaybackTicker()
        startSleepTimerTicker()
    }

    private fun startPlaybackTicker() {
        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            while (true) {
                delay(500)
                if (_isPlaying.value && _durationMs.value > 0) {
                    val advance = (500 * _playbackSpeed.value).toLong()
                    val newPos = _currentPositionMs.value + advance
                    if (newPos >= _durationMs.value) {
                        when (_repeatMode.value) {
                            2 -> { // Repeat One
                                _currentPositionMs.value = 0L
                            }
                            1 -> { // Repeat All
                                nextTrack()
                            }
                            else -> { // Off
                                _isPlaying.value = false
                                _currentPositionMs.value = _durationMs.value
                            }
                        }
                    } else {
                        _currentPositionMs.value = newPos
                    }
                }

                // Video ticker if in video tab
                if (_videoPlaying.value && _currentTab.value == ScreenTab.VIDEO && _videoDurationMs.value > 0) {
                    val advance = (500 * _videoSpeed.value).toLong()
                    val newVideoPos = _videoPositionMs.value + advance
                    if (newVideoPos >= _videoDurationMs.value) {
                        _videoPositionMs.value = 0L
                    } else {
                        _videoPositionMs.value = newVideoPos
                    }
                }
            }
        }
    }

    private fun startSleepTimerTicker() {
        sleepTimerJob?.cancel()
        sleepTimerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                if (_sleepTimerActive.value && _sleepTimerSecondsRemaining.value > 0) {
                    _sleepTimerSecondsRemaining.value -= 1
                    if (_sleepTimerSecondsRemaining.value <= 0) {
                        if (!_sleepTimerWaitForTrackEnd.value) {
                            _isPlaying.value = false
                            _videoPlaying.value = false
                        }
                        _sleepTimerActive.value = false
                    }
                }
            }
        }
    }

    fun playTrack(item: MediaItem) {
        if (item.type == "VIDEO") {
            _currentVideo.value = item
            _videoDurationMs.value = item.durationSeconds * 1000L
            _videoPositionMs.value = 0L
            _videoPlaying.value = true
            _currentTab.value = ScreenTab.VIDEO
        } else {
            _currentTrack.value = item
            _durationMs.value = item.durationSeconds * 1000L
            _currentPositionMs.value = 0L
            _isPlaying.value = true
            _isNowPlayingExpanded.value = true
        }
    }

    fun togglePlayPause() {
        _isPlaying.value = !_isPlaying.value
    }

    fun toggleVideoPlayPause() {
        _videoPlaying.value = !_videoPlaying.value
    }

    fun seekTo(positionMs: Long) {
        _currentPositionMs.value = positionMs.coerceIn(0L, _durationMs.value)
    }

    fun seekVideoTo(positionMs: Long) {
        _videoPositionMs.value = positionMs.coerceIn(0L, _videoDurationMs.value)
    }

    fun skipForward10s() {
        seekTo(_currentPositionMs.value + 10_000L)
    }

    fun skipBackward10s() {
        seekTo(_currentPositionMs.value - 10_000L)
    }

    fun skipVideoForward10s() {
        seekVideoTo(_videoPositionMs.value + 10_000L)
    }

    fun skipVideoBackward10s() {
        seekVideoTo(_videoPositionMs.value - 10_000L)
    }

    fun nextTrack() {
        val list = allMedia.value.filter { it.type == "AUDIO" }
        if (list.isEmpty()) return
        val currentIndex = list.indexOfFirst { it.id == _currentTrack.value?.id }
        val nextIndex = if (_isShuffle.value) {
            (0 until list.size).filter { it != currentIndex }.randomOrNull() ?: 0
        } else {
            (currentIndex + 1) % list.size
        }
        val next = list[nextIndex]
        _currentTrack.value = next
        _durationMs.value = next.durationSeconds * 1000L
        _currentPositionMs.value = 0L
        _isPlaying.value = true
    }

    fun prevTrack() {
        val list = allMedia.value.filter { it.type == "AUDIO" }
        if (list.isEmpty()) return
        val currentIndex = list.indexOfFirst { it.id == _currentTrack.value?.id }
        val prevIndex = if (currentIndex > 0) currentIndex - 1 else list.size - 1
        val prev = list[prevIndex]
        _currentTrack.value = prev
        _durationMs.value = prev.durationSeconds * 1000L
        _currentPositionMs.value = 0L
        _isPlaying.value = true
    }

    fun toggleRepeat() {
        _repeatMode.value = (_repeatMode.value + 1) % 3
    }

    fun toggleShuffle() {
        _isShuffle.value = !_isShuffle.value
    }

    fun cycleSpeed() {
        val speeds = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f)
        val currentIndex = speeds.indexOfFirst { kotlin.math.abs(it - _playbackSpeed.value) < 0.01f }
        val nextIndex = (currentIndex + 1) % speeds.size
        _playbackSpeed.value = speeds[nextIndex]
    }

    fun cycleVideoSpeed() {
        val speeds = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f)
        val currentIndex = speeds.indexOfFirst { kotlin.math.abs(it - _videoSpeed.value) < 0.01f }
        val nextIndex = (currentIndex + 1) % speeds.size
        _videoSpeed.value = speeds[nextIndex]
    }

    fun cycleVideoAspectRatio() {
        val ratios = listOf("16:9", "Fit", "Fill", "4:3")
        val currentIndex = ratios.indexOf(_videoAspectRatio.value)
        _videoAspectRatio.value = ratios[(currentIndex + 1) % ratios.size]
    }

    fun toggleSubtitles() {
        _isSubtitlesOn.value = !_isSubtitlesOn.value
    }

    fun toggleControlsLock() {
        _isControlsLocked.value = !_isControlsLocked.value
    }

    fun toggleBackgroundAudio() {
        _isBackgroundAudio.value = !_isBackgroundAudio.value
    }

    fun setVolume(vol: Float) {
        _volume.value = vol.coerceIn(0f, 1f)
        if (vol > 0f) _isMuted.value = false
    }

    fun toggleMute() {
        _isMuted.value = !_isMuted.value
    }

    fun toggleFavorite(item: MediaItem) {
        viewModelScope.launch {
            repository.toggleFavorite(item)
            if (_currentTrack.value?.id == item.id) {
                _currentTrack.value = _currentTrack.value?.copy(isFavorite = !item.isFavorite)
            }
        }
    }

    fun deleteMedia(item: MediaItem) {
        viewModelScope.launch {
            repository.deleteMedia(item)
            if (_currentTrack.value?.id == item.id) {
                nextTrack()
            }
        }
    }

    fun shareMediaFile(context: Context, item: MediaItem) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = if (item.type == "VIDEO") "video/*" else "audio/*"
            putExtra(Intent.EXTRA_SUBJECT, "Sharing ${item.title}")
            putExtra(Intent.EXTRA_TEXT, "Now playing on Rachidplayer: ${item.title} by ${item.artist} (${item.formatBadge})")
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share ${item.title} via"))
    }

    fun shareApp(context: Context) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Rachidplayer Offline Media Player")
            putExtra(Intent.EXTRA_TEXT, "Check out Rachidplayer: 100% offline, high-resolution audio & 4K video player with zero tracking!")
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Rachidplayer APK"))
    }

    // Rescan storage simulation
    fun rescanStorage() {
        if (_isScanning.value) return
        _isScanning.value = true
        viewModelScope.launch {
            delay(1200)
            _isScanning.value = false
            val count = allMedia.value.size
            _lastScanText.value = "Last scan: Just now • ${if (count > 0) count else 248} items found"
        }
    }

    // Sleep timer controls
    fun toggleSleepTimer() {
        _sleepTimerActive.value = !_sleepTimerActive.value
        if (_sleepTimerActive.value) {
            _sleepTimerSecondsRemaining.value = _sleepTimerMinutes.value * 60L
        }
    }

    fun setSleepTimerPreset(minutes: Int) {
        _sleepTimerMinutes.value = minutes
        _sleepTimerSecondsRemaining.value = minutes * 60L
        _sleepTimerActive.value = true
    }

    fun toggleSleepTimerWaitForTrackEnd() {
        _sleepTimerWaitForTrackEnd.value = !_sleepTimerWaitForTrackEnd.value
    }

    // Stream screen methods
    fun setStreamUrl(url: String) {
        _streamUrlInput.value = url
    }

    fun setPreloadBuffer(enabled: Boolean) {
        _preloadBuffer.value = enabled
    }

    fun loadAndPlayStream() {
        val url = _streamUrlInput.value.trim()
        if (url.isBlank()) return
        val title = url.substringAfterLast("/").ifBlank { "Live Stream" }
        val isVideo = url.endsWith(".mp4") || url.endsWith(".mkv") || url.contains("video")
        val streamType = if (isVideo) "MP4 Video" else if (url.contains("aac") || url.contains("radio")) "Live Radio AAC" else "Audio Stream"

        viewModelScope.launch {
            repository.addStreamHistory(url, title, streamType)
            val streamItem = MediaItem(
                title = title,
                artist = "External Stream",
                album = "Direct Stream URL",
                type = if (isVideo) "VIDEO" else "AUDIO",
                formatBadge = if (isVideo) "Direct MP4" else "Live Stream",
                filePath = url,
                fileSize = "Dynamic Buffer",
                durationSeconds = 1800L,
                formattedDuration = "Live",
                audioStreamSpec = "320kbps CBR Live",
                folder = "Streams",
                artworkUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCj2WcuDsbS-HiMUeC78oErdHLoTx7HmJU7xM8E1NhaO3SchZxf5veJj59t17gstrHg-A1zfn9pPB_RXh7K1PNrRN4jQNLL7iiIalWd0Da2Z2fQOQf9mph69-ywRk5Joe5Uv5Ob0JDfxKMvt5RuiXInAP-Z48vak3HH2AmuEFCe9_UtNQ4AxijMBeT6ryuRJZlMNqdLkSDl3WQH02MIeNhnQeSIL9pOXU1oWbcb1AFLuap55MsmmSEPOw"
            )
            playTrack(streamItem)
        }
    }

    fun deleteStreamHistory(id: Long) {
        viewModelScope.launch {
            repository.deleteStreamHistory(id)
        }
    }

    fun clearAllStreamHistory() {
        viewModelScope.launch {
            repository.clearStreamHistory()
        }
    }

    // Settings actions
    fun setAutoScanOnLaunch(enabled: Boolean) { _autoScanOnLaunch.value = enabled }
    fun setIncludeHiddenFolders(enabled: Boolean) { _includeHiddenFolders.value = enabled }
    fun setTargetedFolders(folders: String) { _targetedFolders.value = folders }
    fun setAudioEngine(engine: String) { _audioEngine.value = engine }
    fun setResumePlayback(enabled: Boolean) { _resumePlayback.value = enabled }
    fun setRememberSpeed(enabled: Boolean) { _rememberSpeed.value = enabled }
    fun setHardwareAccel(enabled: Boolean) { _hardwareAccel.value = enabled }

    fun resetMediaCache() {
        viewModelScope.launch {
            repository.resetDatabase()
            _lastScanText.value = "Cache reset • 248 items restored"
        }
    }
}
