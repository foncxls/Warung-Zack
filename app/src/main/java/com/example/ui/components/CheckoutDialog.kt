package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CartItem
import com.example.ui.theme.RedPrimary
import com.example.ui.theme.StockGreen
import com.example.util.FormatUtils

@Composable
fun CheckoutDialog(
    cartItems: List<CartItem>,
    totalAmount: Long,
    onDismiss: () -> Unit,
    onConfirmCheckout: (cashPaid: Long, paymentMethod: String) -> Unit
) {
    var paymentMethod by remember { mutableStateOf("TUNAI") }
    var cashPaidStr by remember { mutableStateOf(totalAmount.toString()) }
    val cashPaid = cashPaidStr.toLongOrNull() ?: 0L
    val change = if (cashPaid >= totalAmount) cashPaid - totalAmount else 0L
    val isUnderpaid = paymentMethod == "TUNAI" && cashPaid < totalAmount

    // Quick cash presets calculation
    val presets = remember(totalAmount) {
        val list = mutableListOf<Long>()
        list.add(totalAmount) // Uang Pas
        val roundedUp10k = ((totalAmount + 9999) / 10000) * 10000
        if (roundedUp10k > totalAmount && !list.contains(roundedUp10k)) list.add(roundedUp10k)
        val roundedUp50k = ((totalAmount + 49999) / 50000) * 50000
        if (roundedUp50k > totalAmount && !list.contains(roundedUp50k)) list.add(roundedUp50k)
        val roundedUp100k = ((totalAmount + 99999) / 100000) * 100000
        if (roundedUp100k > totalAmount && !list.contains(roundedUp100k)) list.add(roundedUp100k)
        if (!list.contains(50000L) && totalAmount <= 50000L) list.add(50000L)
        if (!list.contains(100000L) && totalAmount <= 100000L) list.add(100000L)
        list.sorted()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = RedPrimary)
                Text(
                    text = "Pembayaran",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Total Tagihan Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "TOTAL TAGIHAN",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = RedPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = FormatUtils.formatRupiah(totalAmount),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${cartItems.sumOf { it.quantity }} item barang",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Metode Pembayaran
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = paymentMethod == "TUNAI",
                        onClick = {
                            paymentMethod = "TUNAI"
                            cashPaidStr = totalAmount.toString()
                        },
                        label = { Text("TUNAI") },
                        leadingIcon = {
                            Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(16.dp))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = RedPrimary,
                            selectedLabelColor = Color.White,
                            selectedLeadingIconColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    FilterChip(
                        selected = paymentMethod == "QRIS",
                        onClick = {
                            paymentMethod = "QRIS"
                            cashPaidStr = totalAmount.toString()
                        },
                        label = { Text("QRIS") },
                        leadingIcon = {
                            Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(16.dp))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = RedPrimary,
                            selectedLabelColor = Color.White,
                            selectedLeadingIconColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                if (paymentMethod == "TUNAI") {
                    // Quick Preset Cash Buttons
                    Text("Pilihan Nominal Uang Diterima:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        presets.take(4).forEach { amount ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (cashPaid == amount) RedPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { cashPaidStr = amount.toString() }
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 2.dp)
                                ) {
                                    Text(
                                        text = if (amount == totalAmount) "Pas" else "${amount / 1000}k",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (cashPaid == amount) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    // Input Tunai Manual
                    OutlinedTextField(
                        value = cashPaidStr,
                        onValueChange = { cashPaidStr = it.filter { c -> c.isDigit() } },
                        label = { Text("Uang Diterima (Rp)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("cash_paid_input")
                    )

                    // Kembalian Display Card
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isUnderpaid) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isUnderpaid) "Uang Kurang:" else "Kembalian:",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = if (isUnderpaid) FormatUtils.formatRupiah(totalAmount - cashPaid) else FormatUtils.formatRupiah(change),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = if (isUnderpaid) MaterialTheme.colorScheme.error else StockGreen
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalPaid = if (paymentMethod == "TUNAI") cashPaid else totalAmount
                    onConfirmCheckout(finalPaid, paymentMethod)
                },
                enabled = !isUnderpaid,
                colors = ButtonDefaults.buttonColors(containerColor = RedPrimary),
                modifier = Modifier.testTag("confirm_checkout_btn")
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.size(6.dp))
                Text("Bayar Sekarang", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_checkout_btn")
            ) {
                Text("Batal")
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}
