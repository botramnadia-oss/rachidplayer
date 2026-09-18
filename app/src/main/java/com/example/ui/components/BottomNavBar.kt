package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Podcasts
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Outline
import com.example.ui.theme.Primary
import com.example.ui.theme.SurfaceContainerHigh
import com.example.viewmodel.ScreenTab

@Composable
fun RachidplayerBottomNavBar(
    selectedTab: ScreenTab,
    onTabSelected: (ScreenTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(SurfaceContainerHigh)
            .navigationBarsPadding()
            .height(64.dp)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavItem(
            label = "Library",
            icon = Icons.Default.LibraryMusic,
            isSelected = selectedTab == ScreenTab.LIBRARY,
            onClick = { onTabSelected(ScreenTab.LIBRARY) },
            testTag = "tab_library"
        )
        BottomNavItem(
            label = "Video",
            icon = Icons.Default.SmartDisplay,
            isSelected = selectedTab == ScreenTab.VIDEO,
            onClick = { onTabSelected(ScreenTab.VIDEO) },
            testTag = "tab_video"
        )
        BottomNavItem(
            label = "Stream",
            icon = Icons.Default.Podcasts,
            isSelected = selectedTab == ScreenTab.STREAM,
            onClick = { onTabSelected(ScreenTab.STREAM) },
            testTag = "tab_stream"
        )
        BottomNavItem(
            label = "Settings",
            icon = Icons.Default.Tune,
            isSelected = selectedTab == ScreenTab.SETTINGS,
            onClick = { onTabSelected(ScreenTab.SETTINGS) },
            testTag = "tab_settings"
        )
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val interactionSource = remember { MutableInteractionSource() }
    val tintColor by animateColorAsState(
        targetValue = if (isSelected) Primary else Outline,
        label = "tintColor"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(9999.dp))
            .background(if (isSelected) Primary.copy(alpha = 0.2f) else androidx.compose.ui.graphics.Color.Transparent)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tintColor,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = tintColor,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
