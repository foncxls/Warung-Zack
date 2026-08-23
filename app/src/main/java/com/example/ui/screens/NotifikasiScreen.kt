package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.data.model.ProductEntity
import com.example.ui.components.RestockDialog
import com.example.ui.theme.RedPrimary
import com.example.ui.theme.StockGreen
import com.example.ui.theme.StockOrange
import com.example.ui.theme.StockRed
import com.example.util.FormatUtils

@Composable
fun NotifikasiScreen(
    lowStockProducts: List<ProductEntity>,
    onQuickRestock: (ProductEntity, Int) -> Unit
) {
    var productForRestockDialog by remember { mutableStateOf<ProductEntity?>(null) }

    val outOfStockItems = remember(lowStockProducts) { lowStockProducts.filter { it.stock <= 0 } }
    val lowStockItems = remember(lowStockProducts) { lowStockProducts.filter { it.stock > 0 } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Status Banner Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (lowStockProducts.isNotEmpty()) MaterialTheme.colorScheme.errorContainer else StockGreen.copy(alpha = 0.15f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (lowStockProducts.isNotEmpty()) RedPrimary else StockGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (lowStockProducts.isNotEmpty()) Icons.Default.NotificationsActive else Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (lowStockProducts.isNotEmpty()) "Peringatan Stok Warung" else "Semua Stok Aman",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (lowStockProducts.isNotEmpty()) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (lowStockProducts.isNotEmpty()) {
                            "${outOfStockItems.size} barang habis, ${lowStockItems.size} barang menipis"
                        } else {
                            "Tidak ada barang yang berada di bawah batas minimum stok."
                        },
                        fontSize = 12.sp,
                        color = if (lowStockProducts.isNotEmpty()) MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (lowStockProducts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = StockGreen,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "Semua persediaan barang tercukupi!",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Sistem akan otomatis memberi notifikasi jika ada stok yang menipis.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("notifikasi_alert_list"),
                contentPadding = PaddingValues(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Out of Stock Section
                if (outOfStockItems.isNotEmpty()) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = StockRed, modifier = Modifier.size(18.dp))
                            Text(
                                text = "STOK HABIS (${outOfStockItems.size})",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = StockRed
                            )
                        }
                    }

                    items(outOfStockItems, key = { "out_${it.id}" }) { product ->
                        LowStockCardItem(
                            product = product,
                            onQuickRestock = { qty -> onQuickRestock(product, qty) },
                            onOpenDialog = { productForRestockDialog = product }
                        )
                    }
                }

                // Low Stock Section
                if (lowStockItems.isNotEmpty()) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = StockOrange, modifier = Modifier.size(18.dp))
                            Text(
                                text = "STOK MENIPIS (${lowStockItems.size})",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = StockOrange
                            )
                        }
                    }

                    items(lowStockItems, key = { "low_${it.id}" }) { product ->
                        LowStockCardItem(
                            product = product,
                            onQuickRestock = { qty -> onQuickRestock(product, qty) },
                            onOpenDialog = { productForRestockDialog = product }
                        )
                    }
                }
            }
        }
    }

    if (productForRestockDialog != null) {
        RestockDialog(
            product = productForRestockDialog!!,
            onDismiss = { productForRestockDialog = null },
            onConfirmRestock = { qty ->
                productForRestockDialog?.let { onQuickRestock(it, qty) }
                productForRestockDialog = null
            }
        )
    }
}

@Composable
fun LowStockCardItem(
    product: ProductEntity,
    onQuickRestock: (Int) -> Unit,
    onOpenDialog: () -> Unit
) {
    val isOut = product.stock <= 0
    val progress = if (product.minStock > 0) {
        (product.stock.toFloat() / (product.minStock * 2f)).coerceIn(0f, 1f)
    } else 0f

    val accentColor = if (isOut) StockRed else StockOrange

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("low_stock_card_${product.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "${product.category} • Modal: ${FormatUtils.formatRupiah(product.buyPrice)}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(accentColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isOut) "HABIS (0 ${product.unit})" else "SISA ${product.stock} ${product.unit}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = accentColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Stock level bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(CircleShape),
                    color = accentColor,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Text(
                    text = "Batas: ${product.minStock} ${product.unit}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Fast Restock Buttons Row (+10, +25, +50, Custom)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Restok:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                listOf(10, 25, 50).forEach { qty ->
                    Button(
                        onClick = { onQuickRestock(qty) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier
                            .height(30.dp)
                            .testTag("quick_restock_${qty}_btn")
                    ) {
                        Text(
                            text = "+$qty",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                OutlinedButton(
                    onClick = onOpenDialog,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(14.dp), tint = RedPrimary)
                    Spacer(modifier = Modifier.size(4.dp))
                    Text("Lainnya", fontSize = 11.sp, color = RedPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
