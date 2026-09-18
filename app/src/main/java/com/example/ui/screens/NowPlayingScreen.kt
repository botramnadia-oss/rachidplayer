package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.components.NowPlayingTopBar
import com.example.ui.theme.NeonCyanGlow
import com.example.ui.theme.NeonIndigoGlow
import com.example.ui.theme.Outline
import com.example.ui.theme.OutlineVariant
import com.example.ui.theme.Primary
import com.example.ui.theme.PrimaryContainer
import com.example.ui.theme.Secondary
import com.example.ui.theme.SecondaryContainer
import com.example.ui.theme.Surface
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerHighest
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.SurfaceContainerLowest
import com.example.viewmodel.PlayerViewModel

@Composable
fun NowPlayingScreen(
    viewModel: PlayerViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentTrack by viewModel.currentTrack.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val positionMs by viewModel.currentPositionMs.collectAsState()
    val durationMs by viewModel.durationMs.collectAsState()
    val repeatMode by viewModel.repeatMode.collectAsState()
    val isShuffle by viewModel.isShuffle.collectAsState()
    val playbackSpeed by viewModel.playbackSpeed.collectAsState()
    val volume by viewModel.volume.collectAsState()
    val sleepTimerMinutes by viewModel.sleepTimerMinutes.collectAsState()
    val isSleepTimerActive by viewModel.sleepTimerActive.collectAsState()
    val allMedia by viewModel.allMedia.collectAsState()

    var showSleepTimerDialog by remember { mutableStateOf(false) }
    var showQueueSheet by remember { mutableStateOf(false) }
    var showEqualizerDialog by remember { mutableStateOf(false) }

    val track = currentTrack ?: return

    val elapsedMinutes = positionMs / 60000
    val elapsedSeconds = (positionMs % 60000) / 1000
    val elapsedText = String.format("%02d:%02d", elapsedMinutes, elapsedSeconds)

    val remainingMs = (durationMs - positionMs).coerceAtLeast(0L)
    val remainingMinutes = remainingMs / 60000
    val remainingSeconds = (remainingMs % 60000) / 1000
    val remainingText = String.format("-%02d:%02d", remainingMinutes, remainingSeconds)

    val progressFraction = if (durationMs > 0) (positionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) else 0f

    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Surface)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Atmospheric glow background
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 40.dp)
                .size(300.dp)
                .clip(CircleShape)
                .background(Primary.copy(alpha = 0.08f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Top Bar
            NowPlayingTopBar(
                sleepTimerMinutes = sleepTimerMinutes,
                isSleepTimerActive = isSleepTimerActive,
                onBackClick = onBackClick,
                onSleepTimerClick = { showSleepTimerDialog = true },
                onEqualizerClick = { showEqualizerDialog = true },
                onShareClick = { viewModel.shareMediaFile(context, track) }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                // Center Hero: Album Art Vinyl Card with Luminescent Glow
                Box(
                    modifier = Modifier
                        .size(270.dp)
                        .shadow(24.dp, RoundedCornerShape(20.dp), spotColor = NeonIndigoGlow)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.White.copy(alpha = 0.12f), Color.Transparent)
                            )
                        )
                        .padding(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(18.dp))
                            .background(SurfaceContainerLow)
                    ) {
                        if (track.artworkUrl.isNotEmpty()) {
                            AsyncImage(
                                model = track.artworkUrl,
                                contentDescription = track.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        // High-Gloss Vinyl Highlight Overlay
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.White.copy(alpha = 0.05f),
                                            Color.White.copy(alpha = 0.1f)
                                        )
                                    )
                                )
                        )

                        // Live Audio Stream Tag Overlay
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(10.dp)
                                .clip(RoundedCornerShape(9999.dp))
                                .background(SurfaceContainerLowest.copy(alpha = 0.85f))
                                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(9999.dp))
                                .padding(horizontal = 10.dp, vertical = 3.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Secondary)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "OFFLINE • LOSSLESS",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    letterSpacing = 0.8.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Track Title & Favorite Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = track.title.removeSuffix(".flac").removeSuffix(".mp3").removeSuffix(".wav"),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = (-0.5).sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { viewModel.toggleFavorite(track) },
                        modifier = Modifier.testTag("btn_favorite_track")
                    ) {
                        Icon(
                            imageVector = if (track.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (track.isFavorite) Secondary else Outline,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Subtitle
                Text(
                    text = "${track.artist}  •  Album: ${track.album.ifEmpty { "Neon Horizon (2024)" }}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Hi-Res Audiophile Spec Badge Chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(9999.dp))
                        .background(SurfaceContainerHigh)
                        .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(9999.dp))
                        .padding(horizontal = 14.dp, vertical = 5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = "Hi-Res",
                            tint = Secondary,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = track.audioStreamSpec.ifEmpty { "FLAC 24-bit / 96kHz Lossless" },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Secondary,
                            letterSpacing = 0.3.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "•  ${track.fileSize}",
                            fontSize = 11.sp,
                            color = Outline
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Waveform Visualizer Decorator
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(26.dp)
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    val heights = listOf(8, 12, 16, 24, 20, 16, 24, 12, 20, 16, 24, 12, 20, 8, 16, 20, 12, 24, 8, 16, 12, 20, 8, 16, 12, 6)
                    heights.forEachIndexed { index, h ->
                        val barColor = when {
                            index < 5 -> SecondaryContainer
                            index < 9 -> Secondary
                            index < 14 -> PrimaryContainer
                            else -> SurfaceContainerHighest
                        }
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(h.dp)
                                .clip(RoundedCornerShape(9999.dp))
                                .background(barColor)
                        )
                    }
                }

                // Interactive Scrubber Slider
                Slider(
                    value = progressFraction,
                    onValueChange = { frac ->
                        viewModel.seekTo((frac * durationMs).toLong())
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = Secondary,
                        inactiveTrackColor = SurfaceContainerHighest
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("scrubber_slider")
                )

                // Elapsed & Remaining Time Indicators
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = elapsedText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "HI-FI BITSTREAM",
                        fontSize = 10.sp,
                        color = OutlineVariant,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = remainingText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Outline,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Primary Playback Transport Deck
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Repeat Toggle
                    IconButton(
                        onClick = { viewModel.toggleRepeat() },
                        modifier = Modifier.testTag("btn_repeat_toggle")
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = if (repeatMode == 2) Icons.Default.RepeatOne else Icons.Default.Repeat,
                                contentDescription = "Repeat",
                                tint = if (repeatMode > 0) Secondary else Outline,
                                modifier = Modifier.size(24.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .padding(top = 2.dp)
                                    .size(4.dp)
                                    .clip(CircleShape)
                                    .background(if (repeatMode > 0) Secondary else Color.Transparent)
                            )
                        }
                    }

                    // Replay 10s
                    IconButton(
                        onClick = { viewModel.skipBackward10s() },
                        modifier = Modifier.testTag("btn_replay_10")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Replay10,
                            contentDescription = "Replay 10s",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // Previous Track
                    IconButton(
                        onClick = { viewModel.prevTrack() },
                        modifier = Modifier.testTag("btn_prev_track")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipPrevious,
                            contentDescription = "Previous Track",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    // Prominent Center Play/Pause Hero Button (64px) with Electric Glow
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .shadow(16.dp, CircleShape, spotColor = NeonIndigoGlow)
                            .clip(CircleShape)
                            .background(PrimaryContainer)
                            .clickable { viewModel.togglePlayPause() }
                            .testTag("btn_hero_play"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(34.dp)
                        )
                    }

                    // Next Track
                    IconButton(
                        onClick = { viewModel.nextTrack() },
                        modifier = Modifier.testTag("btn_next_track")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Next Track",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    // Forward 10s
                    IconButton(
                        onClick = { viewModel.skipForward10s() },
                        modifier = Modifier.testTag("btn_forward_10")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Forward10,
                            contentDescription = "Forward 10s",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // Shuffle Toggle
                    IconButton(
                        onClick = { viewModel.toggleShuffle() },
                        modifier = Modifier.testTag("btn_shuffle_toggle")
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Shuffle,
                                contentDescription = "Shuffle",
                                tint = if (isShuffle) Secondary else Outline,
                                modifier = Modifier.size(24.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .padding(top = 2.dp)
                                    .size(4.dp)
                                    .clip(CircleShape)
                                    .background(if (isShuffle) Secondary else Color.Transparent)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Secondary Android Control Deck (Bento Card)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceContainer)
                        .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Top Row: Speed Selector & Quick Sleep Timer
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Speed Chip
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(9999.dp))
                                    .background(SurfaceContainerHigh)
                                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(9999.dp))
                                    .clickable { viewModel.cycleSpeed() }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                                    .testTag("btn_speed_select")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Speed,
                                        contentDescription = "Speed",
                                        tint = Outline,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "SPEED: ",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Outline,
                                        letterSpacing = 0.8.sp
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Primary.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "${playbackSpeed}x",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Primary
                                        )
                                    }
                                }
                            }

                            // Sleep Timer Quick Trigger
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(9999.dp))
                                    .background(SurfaceContainerHigh)
                                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(9999.dp))
                                    .clickable { showSleepTimerDialog = true }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                    .testTag("btn_sleep_quick")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Snooze,
                                        contentDescription = "Sleep timer",
                                        tint = Secondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Set Sleep Timer",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        // Middle Row: Volume Slider Deck
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(
                                onClick = { viewModel.toggleMute() },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeDown,
                                    contentDescription = "Mute",
                                    tint = Outline,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Slider(
                                value = volume,
                                onValueChange = { viewModel.setVolume(it) },
                                colors = SliderDefaults.colors(
                                    thumbColor = Color.White,
                                    activeTrackColor = Secondary,
                                    inactiveTrackColor = SurfaceContainerHighest
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("volume_slider")
                            )

                            IconButton(
                                onClick = { viewModel.setVolume(1.0f) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Max Volume",
                                    tint = Outline,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Text(
                                text = "${(volume * 100).toInt()}%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Outline,
                                modifier = Modifier.width(36.dp)
                            )
                        }

                        // Bottom Action Row: Native Share Intent Trigger Button
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(SurfaceContainerHigh)
                                .clickable { viewModel.shareMediaFile(context, track) }
                                .padding(vertical = 10.dp, horizontal = 14.dp)
                                .testTag("btn_share_file"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.FileUpload,
                                    contentDescription = "Share",
                                    tint = Secondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Share Lossless Audio File",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "(INTENT)",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Outline,
                                    letterSpacing = 0.8.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bottom Sheet Drawer Tab: Up Next (12 tracks)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                        .background(SurfaceContainerHigh)
                        .clickable { showQueueSheet = true }
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .testTag("drawer_up_next")
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Drag handle
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .width(36.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(9999.dp))
                                .background(Outline.copy(alpha = 0.4f))
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SurfaceContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.QueueMusic,
                                        contentDescription = "Queue",
                                        tint = Secondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Up Next (${allMedia.filter { it.type == "AUDIO" }.size} tracks)",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Tap to inspect queue • Next: Deep Focus Session",
                                        fontSize = 11.sp,
                                        color = Outline
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.Default.ExpandLess,
                                contentDescription = "Expand",
                                tint = Outline,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }

    // Sleep Timer Dialog
    if (showSleepTimerDialog) {
        AlertDialog(
            onDismissRequest = { showSleepTimerDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Bedtime, contentDescription = null, tint = Secondary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sleep Timer", color = MaterialTheme.colorScheme.onSurface)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Automatically suspend playback after:", color = Outline, fontSize = 13.sp)
                    listOf(15, 30, 45, 60).forEach { mins ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (sleepTimerMinutes == mins && isSleepTimerActive) Secondary.copy(alpha = 0.2f) else SurfaceContainer)
                                .clickable {
                                    viewModel.setSleepTimerPreset(mins)
                                    showSleepTimerDialog = false
                                }
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "$mins Minutes",
                                fontWeight = FontWeight.SemiBold,
                                color = if (sleepTimerMinutes == mins && isSleepTimerActive) Secondary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.toggleSleepTimer()
                    showSleepTimerDialog = false
                }) {
                    Text(if (isSleepTimerActive) "Turn Off Timer" else "Start Timer", color = Primary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSleepTimerDialog = false }) {
                    Text("Cancel", color = Outline)
                }
            },
            containerColor = SurfaceContainerHigh
        )
    }

    // Queue Dialog / Sheet
    if (showQueueSheet) {
        val audioTracks = allMedia.filter { it.type == "AUDIO" }
        AlertDialog(
            onDismissRequest = { showQueueSheet = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.QueueMusic, contentDescription = null, tint = Secondary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Up Next Queue", color = MaterialTheme.colorScheme.onSurface)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    audioTracks.forEach { audio ->
                        val isPlayingThis = currentTrack?.id == audio.id
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isPlayingThis) Primary.copy(alpha = 0.15f) else SurfaceContainer)
                                .clickable {
                                    viewModel.playTrack(audio)
                                    showQueueSheet = false
                                }
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = audio.title,
                                        fontWeight = if (isPlayingThis) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isPlayingThis) Primary else MaterialTheme.colorScheme.onSurface,
                                        fontSize = 13.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${audio.artist} • ${audio.formattedDuration}",
                                        fontSize = 11.sp,
                                        color = Outline
                                    )
                                }
                                if (isPlayingThis) {
                                    Text("PLAYING", color = Primary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showQueueSheet = false }) {
                    Text("Close", color = Primary)
                }
            },
            containerColor = SurfaceContainerHigh
        )
    }

    // Equalizer Mock Dialog
    if (showEqualizerDialog) {
        AlertDialog(
            onDismissRequest = { showEqualizerDialog = false },
            title = { Text("Parametric Equalizer", color = MaterialTheme.colorScheme.onSurface) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Hardware Audio DSP Preset: Flat / Pure Bitstream", color = Outline, fontSize = 13.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        listOf("60Hz", "230Hz", "910Hz", "4kHz", "14kHz").forEach { band ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .width(6.dp)
                                        .height(70.dp)
                                        .clip(RoundedCornerShape(9999.dp))
                                        .background(SurfaceContainerHighest)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(35.dp)
                                            .align(Alignment.BottomCenter)
                                            .background(Secondary)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(band, fontSize = 10.sp, color = Outline)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showEqualizerDialog = false }) {
                    Text("Done", color = Primary)
                }
            },
            containerColor = SurfaceContainerHigh
        )
    }
}
