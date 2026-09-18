package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.MiniPlayerBar
import com.example.ui.components.RachidplayerBottomNavBar
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.NowPlayingScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StreamScreen
import com.example.ui.screens.VideoPlayerScreen
import com.example.viewmodel.PlayerViewModel
import com.example.viewmodel.ScreenTab

@Composable
fun RachidplayerApp(
    viewModel: PlayerViewModel = viewModel()
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val isNowPlayingExpanded by viewModel.isNowPlayingExpanded.collectAsState()
    val currentTrack by viewModel.currentTrack.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val positionMs by viewModel.currentPositionMs.collectAsState()
    val durationMs by viewModel.durationMs.collectAsState()

    val progressFraction = if (durationMs > 0) (positionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) else 0f

    BackHandler(enabled = isNowPlayingExpanded) {
        viewModel.setNowPlayingExpanded(false)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            contentWindowInsets = WindowInsets.statusBars,
            bottomBar = {
                if (!isNowPlayingExpanded) {
                    RachidplayerBottomNavBar(
                        selectedTab = currentTab,
                        onTabSelected = { viewModel.setTab(it) }
                    )
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (currentTab) {
                    ScreenTab.LIBRARY -> LibraryScreen(viewModel = viewModel)
                    ScreenTab.VIDEO -> VideoPlayerScreen(viewModel = viewModel)
                    ScreenTab.STREAM -> StreamScreen(viewModel = viewModel)
                    ScreenTab.SETTINGS -> SettingsScreen(viewModel = viewModel)
                }

                // Docked Floating Mini-Player Bar (shown when audio is available & not in video tab or if in stream tab)
                if (currentTab != ScreenTab.VIDEO && currentTrack != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 8.dp)
                    ) {
                        MiniPlayerBar(
                            currentTrack = currentTrack,
                            isPlaying = isPlaying,
                            progressFraction = progressFraction,
                            currentTab = currentTab,
                            onBarClick = { viewModel.setNowPlayingExpanded(true) },
                            onPlayPauseClick = { viewModel.togglePlayPause() },
                            onNextClick = { viewModel.nextTrack() },
                            onDismissClick = { viewModel.togglePlayPause() },
                            onReplay10Click = { viewModel.skipBackward10s() }
                        )
                    }
                }
            }
        }

        // Fullscreen Now Playing Overlay Deck with slide transition
        AnimatedVisibility(
            visible = isNowPlayingExpanded,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            NowPlayingScreen(
                viewModel = viewModel,
                onBackClick = { viewModel.setNowPlayingExpanded(false) }
            )
        }
    }
}
