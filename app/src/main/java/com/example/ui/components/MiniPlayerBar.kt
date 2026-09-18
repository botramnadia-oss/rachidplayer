package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Podcasts
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import com.example.ui.theme.Primary
import com.example.ui.theme.PrimaryContainer
import com.example.ui.theme.Secondary
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceVariant
import com.example.viewmodel.ScreenTab

@Composable
fun MiniPlayerBar(
    currentTrack: MediaItem?,
    isPlaying: Boolean,
    progressFraction: Float,
    currentTab: ScreenTab,
    onBarClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onDismissClick: () -> Unit,
    onReplay10Click: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (currentTrack == null && currentTab != ScreenTab.STREAM) return

    val isStreamTab = currentTab == ScreenTab.STREAM

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .shadow(16.dp, RoundedCornerShape(16.dp), spotColor = NeonIndigoGlow)
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceContainerHigh.copy(alpha = 0.95f))
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
            .clickable(onClick = onBarClick)
            .testTag("mini_player_bar")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: Thumbnail & Title Stack
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Artwork / Thumbnail Slot
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(SurfaceVariant)
                            .border(1.dp, Primary.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isStreamTab) {
                            Icon(
                                imageVector = Icons.Default.Podcasts,
                                contentDescription = "Stream",
                                tint = Secondary,
                                modifier = Modifier.size(22.dp)
                            )
                        } else if (!currentTrack?.artworkUrl.isNullOrEmpty()) {
                            AsyncImage(
                                model = currentTrack!!.artworkUrl,
                                contentDescription = "Cover Art",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.matchParentSize()
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = "Cover",
                                tint = Primary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        // Live cyan dot
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(4.dp)
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Secondary)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Title & Specs
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isStreamTab) "podcast_ep42.mp3" else (currentTrack?.title ?: "Midnight Echoes.flac"),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Primary.copy(alpha = 0.2f))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = if (isStreamTab) "LIVE" else (currentTrack?.formatBadge ?: "FLAC").take(4),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = Primary
                                )
                            }
                        }

                        Text(
                            text = if (isStreamTab) "BUFFERED • 320kbps CBR Live" else "${currentTrack?.artist ?: "The Synthetics"} • 24-bit/96kHz",
                            fontSize = 12.sp,
                            color = Outline,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Right Transport Controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (isStreamTab) {
                        IconButton(
                            onClick = onReplay10Click,
                            modifier = Modifier.size(36.dp).testTag("mini_replay_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Replay10,
                                contentDescription = "Replay 10s",
                                tint = Outline,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Play/Pause Hero Button
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .shadow(8.dp, CircleShape, spotColor = NeonIndigoGlow)
                            .clip(CircleShape)
                            .background(PrimaryContainer)
                            .clickable(onClick = onPlayPauseClick)
                            .testTag("mini_play_pause_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    if (!isStreamTab) {
                        IconButton(
                            onClick = onNextClick,
                            modifier = Modifier.size(36.dp).testTag("mini_next_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipNext,
                                contentDescription = "Next Track",
                                tint = Outline,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        IconButton(
                            onClick = onDismissClick,
                            modifier = Modifier.size(36.dp).testTag("mini_close_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Mini Player",
                                tint = Outline,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Scrubber Progress Line at Bottom Border
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(SurfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progressFraction.coerceIn(0f, 1f))
                        .height(3.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Primary, Secondary)
                            )
                        )
                )
            }
        }
    }
}
