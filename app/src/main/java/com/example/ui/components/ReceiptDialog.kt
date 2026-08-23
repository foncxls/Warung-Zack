package com.example.ui.components

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransactionWithItems
import com.example.ui.theme.RedPrimary
import com.example.ui.theme.StockGreen
import com.example.util.FormatUtils

@Composable
fun ReceiptDialog(
    transactionWithItems: TransactionWithItems,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val tx = transactionWithItems.transaction
    val items = transactionWithItems.items

    val receiptText = buildString {
        appendLine("================================")
        appendLine("         WARUNG ZACK            ")
        appendLine("   Solusi Belanja Harian Hemat  ")
        appendLine("================================")
        appendLine("No. Trx : ${tx.transactionCode}")
        appendLine("Waktu   : ${FormatUtils.formatDateTime(tx.timestamp)}")
        appendLine("Metode  : ${tx.paymentMethod}")
        appendLine("--------------------------------")
        for (item in items) {
            appendLine("${item.productName}")
            appendLine("  ${item.quantity}x @${FormatUtils.formatRupiah(item.sellPrice)} = ${FormatUtils.formatRupiah(item.subtotal)}")
        }
        appendLine("--------------------------------")
        appendLine("TOTAL       : ${FormatUtils.formatRupiah(tx.totalAmount)}")
        appendLine("BAYAR       : ${FormatUtils.formatRupiah(tx.cashPaid)}")
        appendLine("KEMBALI     : ${FormatUtils.formatRupiah(tx.changeAmount)}")
        appendLine("================================")
        appendLine(" Terima Kasih Atas Kunjungan Anda ")
        appendLine("================================")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = null,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Success Badge
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(StockGreen.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = StockGreen,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Transaksi Berhasil!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = tx.transactionCode,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Receipt Slip Paper Card
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Waktu", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(FormatUtils.formatDateTime(tx.timestamp), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Metode", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(tx.paymentMethod, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RedPrimary)
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                        // Items List
                        items.forEach { item ->
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = item.productName,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = FormatUtils.formatRupiah(item.subtotal),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = "${item.quantity} x ${FormatUtils.formatRupiah(item.sellPrice)}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                        // Summary rows
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Belanja", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = FormatUtils.formatRupiah(tx.totalAmount),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = RedPrimary
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Bayar", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(FormatUtils.formatRupiah(tx.cashPaid), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Kembalian", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = FormatUtils.formatRupiah(tx.changeAmount),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = StockGreen
                            )
                        }

                        // Laba info
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(StockGreen.copy(alpha = 0.1f))
                                .padding(vertical = 4.dp, horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Laba Bersih: ${FormatUtils.formatRupiah(tx.totalProfit)}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = StockGreen
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = RedPrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("close_receipt_btn")
            ) {
                Text("Selesai & Lanjut", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = {
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, receiptText)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, "Bagikan Struk Warung Zack")
                    context.startActivity(shareIntent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("share_receipt_btn")
            ) {
                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.size(6.dp))
                Text("Bagikan Struk")
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}
