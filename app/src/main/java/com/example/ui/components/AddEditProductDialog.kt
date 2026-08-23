package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProductEntity
import com.example.ui.theme.RedPrimary

val DEFAULT_CATEGORIES = listOf("Sembako", "Minuman", "Makanan/Snack", "Kebersihan", "Rokok", "Lainnya")
val DEFAULT_UNITS = listOf("Pcs", "Kg", "Bks", "Btl", "Sak", "Pouch", "Dus")

@Composable
fun AddEditProductDialog(
    product: ProductEntity?,
    onDismiss: () -> Unit,
    onSave: (
        id: Int,
        name: String,
        category: String,
        buyPrice: Long,
        sellPrice: Long,
        stock: Int,
        minStock: Int,
        unit: String
    ) -> Unit
) {
    var name by remember { mutableStateOf(product?.name ?: "") }
    var category by remember { mutableStateOf(product?.category ?: "Sembako") }
    var buyPriceStr by remember { mutableStateOf(product?.buyPrice?.toString() ?: "") }
    var sellPriceStr by remember { mutableStateOf(product?.sellPrice?.toString() ?: "") }
    var stockStr by remember { mutableStateOf(product?.stock?.toString() ?: "10") }
    var minStockStr by remember { mutableStateOf(product?.minStock?.toString() ?: "5") }
    var unit by remember { mutableStateOf(product?.unit ?: "Pcs") }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (product == null) "Tambah Barang Baru" else "Edit Barang",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp
                    )
                }

                // Nama Barang
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        errorMessage = null
                    },
                    label = { Text("Nama Barang") },
                    leadingIcon = {
                        Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = RedPrimary)
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("product_name_input")
                )

                // Category Chips
                Text("Kategori", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    DEFAULT_CATEGORIES.forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = RedPrimary
                            )
                        )
                    }
                }

                // Harga Beli (Modal) & Harga Jual
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = buyPriceStr,
                        onValueChange = { buyPriceStr = it.filter { c -> c.isDigit() } },
                        label = { Text("Harga Modal") },
                        leadingIcon = {
                            Icon(Icons.Default.Payments, contentDescription = null, tint = RedPrimary)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("product_buy_price_input")
                    )

                    OutlinedTextField(
                        value = sellPriceStr,
                        onValueChange = { sellPriceStr = it.filter { c -> c.isDigit() } },
                        label = { Text("Harga Jual") },
                        leadingIcon = {
                            Icon(Icons.Default.Sell, contentDescription = null, tint = RedPrimary)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("product_sell_price_input")
                    )
                }

                // Stok Saat Ini & Batas Minimum
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = stockStr,
                        onValueChange = { stockStr = it.filter { c -> c.isDigit() } },
                        label = { Text("Stok Awal") },
                        leadingIcon = {
                            Icon(Icons.Default.Inventory, contentDescription = null, tint = RedPrimary)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("product_stock_input")
                    )

                    OutlinedTextField(
                        value = minStockStr,
                        onValueChange = { minStockStr = it.filter { c -> c.isDigit() } },
                        label = { Text("Min Stok Alert") },
                        leadingIcon = {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = RedPrimary)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("product_min_stock_input")
                    )
                }

                // Satuan Unit
                Text("Satuan", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    DEFAULT_UNITS.forEach { u ->
                        FilterChip(
                            selected = unit == u,
                            onClick = { unit = u },
                            label = { Text(u, fontSize = 12.sp) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank()) {
                        errorMessage = "Nama barang tidak boleh kosong!"
                        return@Button
                    }
                    val buyPrice = buyPriceStr.toLongOrNull() ?: 0L
                    val sellPrice = sellPriceStr.toLongOrNull() ?: 0L
                    if (sellPrice <= 0) {
                        errorMessage = "Harga jual harus lebih dari 0!"
                        return@Button
                    }
                    val stock = stockStr.toIntOrNull() ?: 0
                    val minStock = minStockStr.toIntOrNull() ?: 5

                    onSave(
                        product?.id ?: 0,
                        name,
                        category,
                        buyPrice,
                        sellPrice,
                        stock,
                        minStock,
                        unit
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = RedPrimary),
                modifier = Modifier.testTag("save_product_confirm_btn")
            ) {
                Text("Simpan", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_product_btn")
            ) {
                Text("Batal")
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}
