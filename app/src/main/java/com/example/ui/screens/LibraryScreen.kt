package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.MediaItem
import com.example.ui.components.LibraryTopBar
import com.example.ui.theme.Outline
import com.example.ui.theme.OutlineVariant
import com.example.ui.theme.Primary
import com.example.ui.theme.Secondary
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerHighest
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.SurfaceContainerLowest
import com.example.ui.theme.SurfaceVariant
import com.example.ui.theme.Tertiary
import com.example.viewmodel.PlayerViewModel

@Composable
fun LibraryScreen(
    viewModel: PlayerViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isScanning by viewModel.isScanning.collectAsState()
    val lastScanText by viewModel.lastScanText.collectAsState()
    val mediaList by viewModel.filteredMedia.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val sortOption by viewModel.sortOption.collectAsState()
    val currentTrack by viewModel.currentTrack.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()

    var showSortMenu by remember { mutableStateOf(false) }
    var selectedItemForDetails by remember { mutableStateOf<MediaItem?>(null) }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        LibraryTopBar(
            isScanning = isScanning,
            lastScanText = lastScanText,
            onSearchClick = { /* Focus or scroll to search */ },
            onRescanClick = { viewModel.rescanStorage() }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                // HERO BENTO: Storage Scanner Telemetry Card
                StorageScannerTelemetryCard(
                    onRescanClick = { viewModel.rescanStorage() }
                )
            }

            item {
                // Horizontal Filter Chips
                HorizontalFilterChips(
                    selectedFilter = selectedFilter,
                    totalCount = mediaList.size,
                    onFilterSelect = { viewModel.setFilter(it) }
                )
            }

            item {
                // SEARCH & SORT BAR
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Search Input
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceContainerLow)
                            .border(1.dp, OutlineVariant.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    ) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { viewModel.setSearchQuery(it) },
                            placeholder = {
                                Text(
                                    text = "Search titles, artists, folders...",
                                    fontSize = 13.sp,
                                    color = Outline
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = Outline,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Clear",
                                            tint = Outline,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("search_input_field")
                        )
                    }

                    // Sort Dropdown Button
                    Box {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(SurfaceContainerHigh)
                                .border(1.dp, OutlineVariant.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .clickable { showSortMenu = true }
                                .padding(horizontal = 10.dp, vertical = 12.dp)
                                .testTag("btn_sort_dropdown")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Sort by: ",
                                    fontSize = 11.sp,
                                    color = Outline
                                )
                                Text(
                                    text = sortOption,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Secondary
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Sort options",
                                    tint = Secondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false },
                            modifier = Modifier.background(SurfaceContainerHigh)
                        ) {
                            listOf("Date Added", "Title", "Size", "Duration").forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option, color = MaterialTheme.colorScheme.onSurface) },
                                    onClick = {
                                        viewModel.setSortOption(option)
                                        showSortMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // LOCAL MEDIA LIST
            items(items = mediaList, key = { it.id }) { item ->
                val isCurrentPlaying = currentTrack?.id == item.id && isPlaying
                MediaItemCard(
                    item = item,
                    isCurrentPlaying = isCurrentPlaying,
                    onClick = { viewModel.playTrack(item) },
                    onFavoriteClick = { viewModel.toggleFavorite(item) },
                    onShareClick = { viewModel.shareMediaFile(context, item) },
                    onDeleteClick = { viewModel.deleteMedia(item) },
                    onDetailsClick = { selectedItemForDetails = item }
                )
            }

            item {
                // Bottom spacing for mini-player
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }

    // File Details Dialog
    selectedItemForDetails?.let { item ->
        AlertDialog(
            onDismissRequest = { selectedItemForDetails = null },
            title = {
                Text(
                    text = item.title,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Type: ${item.type}", color = Outline, fontSize = 13.sp)
                    Text("Format: ${item.formatBadge}", color = Outline, fontSize = 13.sp)
                    Text("Duration: ${item.formattedDuration}", color = Outline, fontSize = 13.sp)
                    Text("Size: ${item.fileSize}", color = Outline, fontSize = 13.sp)
                    Text("Path: ${item.filePath}", color = Outline, fontSize = 12.sp)
                    if (item.audioStreamSpec.isNotEmpty()) {
                        Text("Stream Spec: ${item.audioStreamSpec}", color = Secondary, fontSize = 13.sp)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedItemForDetails = null }) {
                    Text("Close", color = Primary)
                }
            },
            containerColor = SurfaceContainerHigh
        )
    }
}

@Composable
private fun StorageScannerTelemetryCard(
    onRescanClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceContainer)
            .border(1.dp, OutlineVariant.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Memory,
                            contentDescription = "Scanner",
                            tint = Secondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Device Media Scanner",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "Deep directory indexing active on internal storage",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(9999.dp))
                        .background(SurfaceContainerHigh)
                        .border(1.dp, OutlineVariant.copy(alpha = 0.3f), RoundedCornerShape(9999.dp))
                        .clickable(onClick = onRescanClick)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("btn_rescan_device_media")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = "Sync",
                            tint = Primary,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Rescan Device Media",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Segmented Storage Meter
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(9999.dp))
                    .background(SurfaceContainerHighest)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.20f)
                        .fillMaxSize()
                        .background(Primary)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .fillMaxSize()
                        .background(Secondary)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .fillMaxSize()
                        .background(SurfaceVariant)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 2 Stat Tiles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Audio Stat Tile
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceContainerLow)
                        .border(1.dp, OutlineVariant.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                        .padding(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Primary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AudioFile,
                                contentDescription = "Audio",
                                tint = Primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "184 Audio Files",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "1.2 GB Lossless & HD",
                                fontSize = 11.sp,
                                color = Outline
                            )
                        }
                    }
                }

                // Video Stat Tile
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceContainerLow)
                        .border(1.dp, OutlineVariant.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                        .padding(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Secondary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.VideoFile,
                                contentDescription = "Video",
                                tint = Secondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "64 Videos",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "4.8 GB 4K/FHD MKV",
                                fontSize = 11.sp,
                                color = Outline
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HorizontalFilterChips(
    selectedFilter: String,
    totalCount: Int,
    onFilterSelect: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            label = "All Media ($totalCount)",
            icon = Icons.Default.AllInclusive,
            isSelected = selectedFilter == "ALL",
            onClick = { onFilterSelect("ALL") }
        )
        FilterChip(
            label = "Music (184)",
            icon = Icons.Default.MusicNote,
            isSelected = selectedFilter == "MUSIC",
            onClick = { onFilterSelect("MUSIC") }
        )
        FilterChip(
            label = "Videos (64)",
            icon = Icons.Default.Videocam,
            isSelected = selectedFilter == "VIDEOS",
            onClick = { onFilterSelect("VIDEOS") }
        )
        FilterChip(
            label = "Folders",
            icon = Icons.Default.Folder,
            isSelected = selectedFilter == "FOLDERS",
            onClick = { onFilterSelect("FOLDERS") }
        )
        FilterChip(
            label = "Recently Added",
            icon = Icons.Default.History,
            isSelected = selectedFilter == "RECENT",
            onClick = { onFilterSelect("RECENT") }
        )
    }
}

@Composable
private fun FilterChip(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(9999.dp))
            .background(if (isSelected) Primary else SurfaceContainerHigh)
            .border(
                1.dp,
                if (isSelected) Color.Transparent else OutlineVariant.copy(alpha = 0.3f),
                RoundedCornerShape(9999.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else Primary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun MediaItemCard(
    item: MediaItem,
    isCurrentPlaying: Boolean,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onShareClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onDetailsClick: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (isCurrentPlaying) SurfaceContainer else SurfaceContainerLow)
            .border(
                1.dp,
                if (isCurrentPlaying) Primary.copy(alpha = 0.4f) else OutlineVariant.copy(alpha = 0.2f),
                RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
            .padding(10.dp)
            .testTag("media_card_${item.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: 52px Artwork/Thumbnail Slot + Metadata
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 52px Thumbnail
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceContainerHigh)
                        .border(
                            1.dp,
                            if (isCurrentPlaying) Primary.copy(alpha = 0.5f) else OutlineVariant.copy(alpha = 0.3f),
                            RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (item.artworkUrl.isNotEmpty()) {
                        AsyncImage(
                            model = item.artworkUrl,
                            contentDescription = item.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.matchParentSize()
                        )
                    }

                    if (isCurrentPlaying) {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(Primary.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Equalizer,
                                contentDescription = "Playing",
                                tint = Primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    } else if (item.type == "VIDEO") {
                        if (item.formatBadge.contains("4K")) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(3.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(SurfaceContainerLowest.copy(alpha = 0.9f))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "4K",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Secondary
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .background(Color.Black.copy(alpha = 0.35f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Video",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Metadata 2-line Stack
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        fontSize = 15.sp,
                        fontWeight = if (isCurrentPlaying) FontWeight.Bold else FontWeight.SemiBold,
                        color = if (isCurrentPlaying) Primary else MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = item.artist,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(text = "•", fontSize = 11.sp, color = Outline)

                        // Format Badge Chip
                        val badgeColor = if (item.type == "VIDEO") Secondary else if (item.formatBadge.contains("WAV")) Tertiary else Primary
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(badgeColor.copy(alpha = 0.15f))
                                .border(1.dp, badgeColor.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = item.formatBadge,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = badgeColor
                            )
                        }

                        Text(text = "•", fontSize = 11.sp, color = Outline)
                        Text(text = item.formattedDuration, fontSize = 12.sp, color = Outline)
                        Text(text = "•", fontSize = 11.sp, color = Outline)
                        Text(text = item.fileSize, fontSize = 11.sp, color = Outline)
                    }
                }
            }

            // Right: 3-dot overflow menu
            Box {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = Outline,
                        modifier = Modifier.size(20.dp)
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(SurfaceContainerHigh)
                ) {
                    DropdownMenuItem(
                        text = { Text("Play Now", color = MaterialTheme.colorScheme.onSurface) },
                        leadingIcon = { Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Primary) },
                        onClick = {
                            showMenu = false
                            onClick()
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                if (item.isFavorite) "Remove from Favorites" else "Add to Favorites",
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        leadingIcon = {
                            Icon(
                                if (item.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                tint = if (item.isFavorite) Secondary else Outline
                            )
                        },
                        onClick = {
                            showMenu = false
                            onFavoriteClick()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Share File", color = MaterialTheme.colorScheme.onSurface) },
                        leadingIcon = { Icon(Icons.Default.Sync, contentDescription = null, tint = Secondary) },
                        onClick = {
                            showMenu = false
                            onShareClick()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("File Details", color = MaterialTheme.colorScheme.onSurface) },
                        leadingIcon = { Icon(Icons.Default.Memory, contentDescription = null, tint = Primary) },
                        onClick = {
                            showMenu = false
                            onDetailsClick()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete from Device", color = MaterialTheme.colorScheme.error) },
                        leadingIcon = { Icon(Icons.Default.Clear, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                        onClick = {
                            showMenu = false
                            onDeleteClick()
                        }
                    )
                }
            }
        }
    }
}
