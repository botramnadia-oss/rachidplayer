package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitScreen
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PictureInPictureAlt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import com.example.data.model.MediaItem
import com.example.ui.theme.NeonIndigoGlow
import com.example.ui.theme.Outline
import com.example.ui.theme.OutlineVariant
import com.example.ui.theme.Primary
import com.example.ui.theme.PrimaryContainer
import com.example.ui.theme.Secondary
import com.example.ui.theme.Surface
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerHighest
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.SurfaceContainerLowest
import com.example.viewmodel.PlayerViewModel
import com.example.viewmodel.ScreenTab

@Composable
fun VideoPlayerScreen(
    viewModel: PlayerViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentVideo by viewModel.currentVideo.collectAsState()
    val isPlaying by viewModel.videoPlaying.collectAsState()
    val positionMs by viewModel.videoPositionMs.collectAsState()
    val durationMs by viewModel.videoDurationMs.collectAsState()
    val aspectRatio by viewModel.videoAspectRatio.collectAsState()
    val videoSpeed by viewModel.videoSpeed.collectAsState()
    val isSubtitlesOn by viewModel.isSubtitlesOn.collectAsState()
    val isControlsLocked by viewModel.isControlsLocked.collectAsState()
    val isBackgroundAudio by viewModel.isBackgroundAudio.collectAsState()
    val allMedia by viewModel.allMedia.collectAsState()

    var showMenu by remember { mutableStateOf(false) }
    var showAudioTrackDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    val video = currentVideo ?: allMedia.find { it.type == "VIDEO" } ?: return

    val elapsedMinutes = positionMs / 60000
    val elapsedSeconds = (positionMs % 60000) / 1000
    val elapsedText = String.format("%02d:%02d", elapsedMinutes, elapsedSeconds)

    val durationMinutes = durationMs / 60000
    val durationSeconds = (durationMs % 60000) / 1000
    val durationText = String.format("%02d:%02d", durationMinutes, durationSeconds)

    val progressFraction = if (durationMs > 0) (positionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) else 0f

    val otherVideos = allMedia.filter { it.type == "VIDEO" && it.id != video.id }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Sticky 16:9 Video Player Viewport Frame
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .background(Color.Black)
        ) {
            // Video Frame Media Image / Preview
            if (video.artworkUrl.isNotEmpty()) {
                AsyncImage(
                    model = video.artworkUrl,
                    contentDescription = video.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Top / Bottom Vignette Gradients
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.75f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
            )

            if (!isControlsLocked) {
                // Top Overlay Bar: Back button, Title & Specs, CC, Audio, Menu
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        IconButton(
                            onClick = { viewModel.setTab(ScreenTab.LIBRARY) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        Column {
                            Text(
                                text = video.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${video.videoResolution.ifEmpty { "3840x2160" }} • ${video.videoFps.let { if (it > 0) "${it} FPS" else "60 FPS" }} • ${video.videoCodec.ifEmpty { "HEVC/H.265" }}",
                                fontSize = 10.sp,
                                color = Secondary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // CC Subtitles
                        IconButton(
                            onClick = { viewModel.toggleSubtitles() },
                            modifier = Modifier.size(36.dp).testTag("btn_cc_subtitles")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Subtitles,
                                contentDescription = "Subtitles",
                                tint = if (isSubtitlesOn) Secondary else Outline,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Audio Track Selector
                        IconButton(
                            onClick = { showAudioTrackDialog = true },
                            modifier = Modifier.size(36.dp).testTag("btn_audio_track")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Audiotrack,
                                contentDescription = "Audio Track",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // 3-dot Menu
                        Box {
                            IconButton(
                                onClick = { showMenu = true },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "Options",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false },
                                modifier = Modifier.background(SurfaceContainerHigh)
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Decoder: HW (MediaCodec)", color = MaterialTheme.colorScheme.onSurface) },
                                    onClick = { showMenu = false }
                                )
                                DropdownMenuItem(
                                    text = { Text("Repeat Video", color = MaterialTheme.colorScheme.onSurface) },
                                    onClick = { showMenu = false }
                                )
                                DropdownMenuItem(
                                    text = { Text("Screenshot Frame", color = MaterialTheme.colorScheme.onSurface) },
                                    onClick = { showMenu = false }
                                )
                            }
                        }
                    }
                }

                // Center Transport Controls
                Row(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalArrangement = Arrangement.spacedBy(28.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.skipVideoBackward10s() },
                        modifier = Modifier.size(44.dp).testTag("video_replay_10")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Replay10,
                            contentDescription = "Replay 10s",
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    // 64px Hero Play/Pause
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .shadow(16.dp, CircleShape, spotColor = NeonIndigoGlow)
                            .clip(CircleShape)
                            .background(PrimaryContainer)
                            .clickable { viewModel.toggleVideoPlayPause() }
                            .testTag("video_hero_play"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    IconButton(
                        onClick = { viewModel.skipVideoForward10s() },
                        modifier = Modifier.size(44.dp).testTag("video_forward_10")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Forward10,
                            contentDescription = "Forward 10s",
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

                // Subtitle Line if active
                if (isSubtitlesOn) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 28.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.Black.copy(alpha = 0.75f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "[Cinematic Ambient Audio • Nature Surround]",
                            fontSize = 11.sp,
                            color = Color.Yellow,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Lock controls toggle button (Bottom Left)
            IconButton(
                onClick = { viewModel.toggleControlsLock() },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(4.dp)
                    .size(32.dp)
                    .testTag("btn_lock_controls")
            ) {
                Icon(
                    imageVector = if (isControlsLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                    contentDescription = "Lock controls",
                    tint = if (isControlsLocked) Secondary else Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(18.dp)
                )
            }

            // Bottom Scrubber Bar along bottom of video frame
            if (!isControlsLocked) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                ) {
                    Slider(
                        value = progressFraction,
                        onValueChange = { frac ->
                            viewModel.seekVideoTo((frac * durationMs).toLong())
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = Secondary,
                            inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(20.dp)
                            .testTag("video_scrubber")
                    )
                }
            }
        }

        // SCROLLABLE BODY
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            // Video Control Bottom Bar: Timestamps, 16:9, 1.25x, PiP, Fullscreen
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceContainerLow)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$elapsedText / $durationText",
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Aspect ratio chip
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(SurfaceContainerHigh)
                            .clickable { viewModel.cycleVideoAspectRatio() }
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                            .testTag("btn_aspect_ratio")
                    ) {
                        Text(
                            text = aspectRatio,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Secondary
                        )
                    }

                    // Speed chip
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(SurfaceContainerHigh)
                            .clickable { viewModel.cycleVideoSpeed() }
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                            .testTag("btn_video_speed")
                    ) {
                        Text(
                            text = "${videoSpeed}x",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                    }

                    // PiP Button
                    IconButton(
                        onClick = { /* PiP handled */ },
                        modifier = Modifier.size(32.dp).testTag("btn_pip")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureInPictureAlt,
                            contentDescription = "PiP",
                            tint = Outline,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Fullscreen Fit
                    IconButton(
                        onClick = { viewModel.cycleVideoAspectRatio() },
                        modifier = Modifier.size(32.dp).testTag("btn_fullscreen")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FitScreen,
                            contentDescription = "Fullscreen",
                            tint = Outline,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons Row: Share Video, Background, Delete File
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceContainerHigh)
                        .clickable { viewModel.shareMediaFile(context, video) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = Secondary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Share Video", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isBackgroundAudio) Primary.copy(alpha = 0.2f) else SurfaceContainerHigh)
                        .clickable { viewModel.toggleBackgroundAudio() }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Headphones, contentDescription = "Background", tint = if (isBackgroundAudio) Primary else Outline, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Background", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = if (isBackgroundAudio) Primary else MaterialTheme.colorScheme.onSurface)
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceContainerHigh)
                        .clickable { showDeleteConfirmDialog = true }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Delete File", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // File Specifications Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SurfaceContainer)
                    .border(1.dp, OutlineVariant.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "File Specifications",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(9999.dp))
                                .background(SurfaceContainerHigh)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Secondary, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Offline Local Storage", fontSize = 10.sp, color = Secondary, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("File Path: ${video.filePath}", fontSize = 12.sp, color = Outline, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text("File Size: ${video.fileSize} • Container: MPEG-4 (.mp4)", fontSize = 12.sp, color = Outline)
                        Text("Video Codec: ${video.videoCodec.ifEmpty { "H.265 (HEVC)" }}", fontSize = 12.sp, color = Secondary)
                        Text("Audio Stream: ${video.audioStreamSpec.ifEmpty { "AAC 48kHz Stereo" }}", fontSize = 12.sp, color = Primary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Other Videos in this Folder Section
            Text(
                text = "Other Videos in this Folder (${video.folder})",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                otherVideos.forEach { other ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceContainerLow)
                            .border(1.dp, OutlineVariant.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .clickable { viewModel.playTrack(other) }
                            .padding(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Thumbnail
                            Box(
                                modifier = Modifier
                                    .size(60.dp, 44.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SurfaceContainerHigh),
                                contentAlignment = Alignment.Center
                            ) {
                                if (other.artworkUrl.isNotEmpty()) {
                                    AsyncImage(
                                        model = other.artworkUrl,
                                        contentDescription = other.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(2.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(Color.Black.copy(alpha = 0.75f))
                                        .padding(horizontal = 3.dp, vertical = 1.dp)
                                ) {
                                    Text(other.formattedDuration, fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = other.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${other.formatBadge} • ${other.fileSize}",
                                    fontSize = 11.sp,
                                    color = Outline
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                tint = Secondary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }

    // Audio Track Selection Dialog
    if (showAudioTrackDialog) {
        AlertDialog(
            onDismissRequest = { showAudioTrackDialog = false },
            title = { Text("Select Audio Stream", color = MaterialTheme.colorScheme.onSurface) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Track 1: AAC 48kHz Stereo (Default)", "Track 2: AC-3 5.1 Surround", "Track 3: Director's Commentary").forEach { track ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (track.contains("Default")) Secondary.copy(alpha = 0.2f) else SurfaceContainer)
                                .clickable { showAudioTrackDialog = false }
                                .padding(12.dp)
                        ) {
                            Text(track, fontSize = 13.sp, color = if (track.contains("Default")) Secondary else MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAudioTrackDialog = false }) {
                    Text("Close", color = Primary)
                }
            },
            containerColor = SurfaceContainerHigh
        )
    }

    // Delete confirmation dialog
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Delete Video", color = MaterialTheme.colorScheme.onSurface) },
            text = { Text("Are you sure you want to delete ${video.title} from device storage?", color = Outline) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteMedia(video)
                    showDeleteConfirmDialog = false
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Cancel", color = Outline)
                }
            },
            containerColor = SurfaceContainerHigh
        )
    }
}
