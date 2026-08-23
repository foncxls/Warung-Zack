package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TopSellingItem
import com.example.data.model.TransactionWithItems
import com.example.ui.theme.RedPrimary
import com.example.ui.theme.StockGreen
import com.example.ui.viewmodel.ReportPeriod
import com.example.util.FormatUtils

@Composable
fun LaporanScreen(
    currentPeriod: ReportPeriod,
    transactions: List<TransactionWithItems>,
    topSellingItems: List<TopSellingItem>,
    onPeriodSelected: (ReportPeriod) -> Unit,
    onDeleteTransaction: (TransactionWithItems) -> Unit
) {
    var txToDelete by remember { mutableStateOf<TransactionWithItems?>(null) }

    // Aggregate statistics
    val totalRevenue = remember(transactions) { transactions.sumOf { it.transaction.totalAmount } }
    val totalCost = remember(transactions) { transactions.sumOf { it.transaction.totalCost } }
    val totalProfit = remember(transactions) { transactions.sumOf { it.transaction.totalProfit } }
    val totalTxCount = transactions.size
    val totalItemsSold = remember(transactions) { transactions.sumOf { it.transaction.totalItemsCount } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Period Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            ReportPeriod.values().forEach { period ->
                FilterChip(
                    selected = currentPeriod == period,
                    onClick = { onPeriodSelected(period) },
                    label = { Text(period.label, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = RedPrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .testTag("laporan_list"),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Main Financial Summary Card (Hero Red Card)
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = RedPrimary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "LAPORAN KEUANGAN (${currentPeriod.label.uppercase()})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        // Omzet Penjualan
                        Text(
                            text = "Omzet Penjualan",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Text(
                            text = FormatUtils.formatRupiah(totalRevenue),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(10.dp))

                        // Sub stats row: Modal & Laba Bersih
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Total Modal (HPP)", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                                Text(
                                    text = FormatUtils.formatRupiah(totalCost),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Laba Bersih Untung", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                                Text(
                                    text = FormatUtils.formatRupiah(totalProfit),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFA5D6A7) // Light Mint Green
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Tx count and items sold badge
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.White.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "$totalTxCount Transaksi",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.White.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "$totalItemsSold Item Terjual",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Top Selling Products Section
            if (topSellingItems.isNotEmpty()) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.Leaderboard, contentDescription = null, tint = RedPrimary, modifier = Modifier.size(20.dp))
                        Text(
                            text = "Barang Terlaris",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            topSellingItems.take(5).forEachIndexed { index, item ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(if (index == 0) RedPrimary else MaterialTheme.colorScheme.surfaceVariant),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${index + 1}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (index == 0) Color.White else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                        Column {
                                            Text(
                                                text = item.productName,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Text(
                                                text = "${item.totalQuantitySold} terjual",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = FormatUtils.formatRupiah(item.totalRevenue),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = RedPrimary
                                        )
                                        Text(
                                            text = "Laba: ${FormatUtils.formatRupiah(item.totalProfit)}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = StockGreen
                                        )
                                    }
                                }
                                if (index < minOf(4, topSellingItems.size - 1)) {
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                }
                            }
                        }
                    }
                }
            }

            // Transaction History Section
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = RedPrimary, modifier = Modifier.size(20.dp))
                    Text(
                        text = "Riwayat Transaksi (${transactions.size})",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (transactions.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Belum ada transaksi pada periode ini",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(transactions, key = { it.transaction.id }) { txWithItems ->
                    TransactionHistoryItem(
                        txWithItems = txWithItems,
                        onDeleteClick = { txToDelete = txWithItems }
                    )
                }
            }
        }
    }

    // Delete Transaction Dialog
    if (txToDelete != null) {
        AlertDialog(
            onDismissRequest = { txToDelete = null },
            title = { Text("Batalkan Transaksi?", fontWeight = FontWeight.Bold) },
            text = {
                Text("Batalkan transaksi ${txToDelete?.transaction?.transactionCode}? Stok barang yang terjual akan otomatis dikembalikan ke inventaris.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        txToDelete?.let { onDeleteTransaction(it) }
                        txToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedPrimary),
                    modifier = Modifier.testTag("confirm_delete_tx_btn")
                ) {
                    Text("Batalkan & Kembalikan Stok")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { txToDelete = null }) {
                    Text("Tutup")
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun TransactionHistoryItem(
    txWithItems: TransactionWithItems,
    onDeleteClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val tx = txWithItems.transaction
    val items = txWithItems.items

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .testTag("tx_card_${tx.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Top Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = tx.transactionCode,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = FormatUtils.formatDateTime(tx.timestamp),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = FormatUtils.formatRupiah(tx.totalAmount),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = RedPrimary
                        )
                        Text(
                            text = "Laba: +${FormatUtils.formatRupiah(tx.totalProfit)}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = StockGreen
                        )
                    }

                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Expanded Item Breakdown
            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))

                    items.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${item.productName} (${item.quantity}x)",
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = FormatUtils.formatRupiah(item.subtotal),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Metode: ${tx.paymentMethod} • Bayar: ${FormatUtils.formatRupiah(tx.cashPaid)}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        IconButton(
                            onClick = onDeleteClick,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                Icons.Default.DeleteOutline,
                                contentDescription = "Batalkan",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
