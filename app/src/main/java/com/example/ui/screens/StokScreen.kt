package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import com.example.ui.components.DEFAULT_CATEGORIES
import com.example.ui.theme.RedPrimary
import com.example.ui.theme.StockGreen
import com.example.ui.theme.StockOrange
import com.example.ui.theme.StockRed
import com.example.util.FormatUtils

@Composable
fun StokScreen(
    products: List<ProductEntity>,
    allProductsCount: Int,
    searchQuery: String,
    selectedCategory: String,
    onSearchChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onOpenAddProduct: () -> Unit,
    onOpenEditProduct: (ProductEntity) -> Unit,
    onAdjustStock: (ProductEntity, Int) -> Unit,
    onDeleteProduct: (ProductEntity) -> Unit
) {
    val categories = listOf("Semua") + DEFAULT_CATEGORIES
    var productToDelete by remember { mutableStateOf<ProductEntity?>(null) }

    // Summary calculations
    val totalAssetValue = remember(products) {
        products.sumOf { it.buyPrice * it.stock }
    }
    val criticalCount = remember(products) {
        products.count { it.isCritical }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Header Stats Cards (3 Columns)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Card 1: Total SKU
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Inventory2, contentDescription = null, tint = RedPrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "$allProductsCount", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                        Text(text = "Total Item", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                // Card 2: Total Modal Aset
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.weight(1.3f)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Payments, contentDescription = null, tint = StockGreen, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = FormatUtils.formatRupiah(totalAssetValue), fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                        Text(text = "Nilai Modal Stok", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                // Card 3: Stok Kritis/Habis
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (criticalCount > 0) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.WarningAmber,
                            contentDescription = null,
                            tint = if (criticalCount > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "$criticalCount",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (criticalCount > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                        )
                        Text(text = "Menipis", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("Cari stok produk...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = RedPrimary)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Hapus")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("stok_search_input")
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Category filter chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categories.forEach { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { onCategoryChange(cat) },
                        label = { Text(cat, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = RedPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Stock Items List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("stock_item_list"),
                contentPadding = PaddingValues(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(products, key = { it.id }) { product ->
                    StockRowItem(
                        product = product,
                        onEdit = { onOpenEditProduct(product) },
                        onDelete = { productToDelete = product },
                        onIncrement = { onAdjustStock(product, 1) },
                        onDecrement = { onAdjustStock(product, -1) }
                    )
                }
            }
        }

        // FAB to Add Product
        FloatingActionButton(
            onClick = onOpenAddProduct,
            containerColor = RedPrimary,
            contentColor = Color.White,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("add_product_fab")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Barang")
                Text("Barang Baru", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }

    // Delete Confirmation Dialog
    if (productToDelete != null) {
        AlertDialog(
            onDismissRequest = { productToDelete = null },
            title = { Text("Hapus Barang?", fontWeight = FontWeight.Bold) },
            text = {
                Text("Apakah Anda yakin ingin menghapus '${productToDelete?.name}' dari daftar stok Warung Zack?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        productToDelete?.let { onDeleteProduct(it) }
                        productToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedPrimary),
                    modifier = Modifier.testTag("confirm_delete_product_btn")
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { productToDelete = null }) {
                    Text("Batal")
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun StockRowItem(
    product: ProductEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    val isOut = product.isOutOfStock
    val isLow = product.isLowStock

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("stock_card_${product.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Top Row: Category + Name + Edit/Delete Icons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.category,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = product.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Action Icons
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.DeleteOutline,
                            contentDescription = "Hapus",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Middle Row: Pricing info (Modal, Jual, Laba)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Modal: ${FormatUtils.formatRupiah(product.buyPrice)}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "•",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Jual: ${FormatUtils.formatRupiah(product.sellPrice)}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = RedPrimary
                )
                Text(
                    text = "(+${FormatUtils.formatRupiah(product.profitPerUnit)})",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = StockGreen
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bottom Row: Current Stock & Fast Adjustment Buttons (+/-)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Stock Badge
                val badgeColor = when {
                    isOut -> StockRed
                    isLow -> StockOrange
                    else -> StockGreen
                }
                val statusText = when {
                    isOut -> "STOK HABIS (0)"
                    isLow -> "STOK MENIPIS (${product.stock} ${product.unit})"
                    else -> "Stok: ${product.stock} ${product.unit}"
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(badgeColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = statusText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor
                    )
                }

                // Quick Stock Adjustment Stepper
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IconButton(
                        onClick = onDecrement,
                        enabled = product.stock > 0,
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Kurang Stok", modifier = Modifier.size(16.dp))
                    }

                    Text(
                        text = "${product.stock}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    IconButton(
                        onClick = onIncrement,
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Tambah Stok", modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}
