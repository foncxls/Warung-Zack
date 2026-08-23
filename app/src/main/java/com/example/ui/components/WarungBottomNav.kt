package com.example.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PointOfSale
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.RedPrimary
import com.example.ui.viewmodel.WarungTab

@Composable
fun WarungBottomNav(
    currentTab: WarungTab,
    onTabSelected: (WarungTab) -> Unit,
    lowStockCount: Int,
    cartItemCount: Int
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        modifier = Modifier.testTag("warung_bottom_navigation")
    ) {
        // Tab 1: Kasir (POS)
        NavigationBarItem(
            selected = currentTab == WarungTab.KASIR,
            onClick = { onTabSelected(WarungTab.KASIR) },
            icon = {
                BadgedBox(
                    badge = {
                        if (cartItemCount > 0) {
                            Badge(
                                containerColor = RedPrimary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ) {
                                Text(
                                    text = "$cartItemCount",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (currentTab == WarungTab.KASIR) Icons.Filled.PointOfSale else Icons.Outlined.PointOfSale,
                        contentDescription = "Kasir",
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            label = { Text("Kasir", fontSize = 12.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = RedPrimary,
                selectedTextColor = RedPrimary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.testTag("nav_kasir_tab")
        )

        // Tab 2: Stok (Inventory)
        NavigationBarItem(
            selected = currentTab == WarungTab.STOK,
            onClick = { onTabSelected(WarungTab.STOK) },
            icon = {
                Icon(
                    imageVector = if (currentTab == WarungTab.STOK) Icons.Filled.Inventory2 else Icons.Outlined.Inventory2,
                    contentDescription = "Stok",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Stok", fontSize = 12.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = RedPrimary,
                selectedTextColor = RedPrimary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.testTag("nav_stok_tab")
        )

        // Tab 3: Laporan (Financial Report)
        NavigationBarItem(
            selected = currentTab == WarungTab.LAPORAN,
            onClick = { onTabSelected(WarungTab.LAPORAN) },
            icon = {
                Icon(
                    imageVector = if (currentTab == WarungTab.LAPORAN) Icons.Filled.Assessment else Icons.Outlined.Assessment,
                    contentDescription = "Laporan",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Laporan", fontSize = 12.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = RedPrimary,
                selectedTextColor = RedPrimary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.testTag("nav_laporan_tab")
        )

        // Tab 4: Peringatan Stok
        NavigationBarItem(
            selected = currentTab == WarungTab.NOTIFIKASI,
            onClick = { onTabSelected(WarungTab.NOTIFIKASI) },
            icon = {
                BadgedBox(
                    badge = {
                        if (lowStockCount > 0) {
                            Badge(
                                containerColor = RedPrimary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ) {
                                Text(
                                    text = "$lowStockCount",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (currentTab == WarungTab.NOTIFIKASI) Icons.Filled.NotificationsActive else Icons.Outlined.Notifications,
                        contentDescription = "Peringatan",
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            label = { Text("Peringatan", fontSize = 12.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = RedPrimary,
                selectedTextColor = RedPrimary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.testTag("nav_notifikasi_tab")
        )
    }
}
