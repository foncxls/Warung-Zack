package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.RedPrimary
import com.example.ui.viewmodel.WarungTab

@Composable
fun WarungTopBar(
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    lowStockCount: Int,
    onNotificationClick: () -> Unit,
    currentTab: WarungTab
) {
    Surface(
        color = if (isDarkMode) MaterialTheme.colorScheme.surface else RedPrimary,
        tonalElevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("warung_top_bar")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Brand Logo & Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isDarkMode) RedPrimary else Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Storefront,
                        contentDescription = "Logo",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = "Warung Zack",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Quick Actions: Notification & Dark Mode Switcher
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Low stock alert icon
                IconButton(
                    onClick = onNotificationClick,
                    modifier = Modifier.testTag("top_bar_alert_btn")
                ) {
                    BadgedBox(
                        badge = {
                            if (lowStockCount > 0) {
                                Badge(
                                    containerColor = if (isDarkMode) RedPrimary else Color.White,
                                    contentColor = if (isDarkMode) Color.White else RedPrimary
                                ) {
                                    Text(
                                        text = if (lowStockCount > 99) "99+" else "$lowStockCount",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Peringatan Stok",
                            tint = Color.White
                        )
                    }
                }

                // Dark mode toggle button
                IconButton(
                    onClick = onToggleDarkMode,
                    modifier = Modifier.testTag("dark_mode_toggle_btn")
                ) {
                    Icon(
                        imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = if (isDarkMode) "Mode Terang" else "Mode Gelap",
                        tint = Color.White
                    )
                }
            }
        }
    }
}
