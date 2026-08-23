package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProductEntity
import com.example.ui.theme.RedPrimary

@Composable
fun RestockDialog(
    product: ProductEntity,
    onDismiss: () -> Unit,
    onConfirmRestock: (addQty: Int) -> Unit
) {
    var addQtyStr by remember { mutableStateOf("10") }
    val quickOptions = listOf(5, 10, 20, 50, 100)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.AddShoppingCart, contentDescription = null, tint = RedPrimary)
                Text(
                    text = "Restok Barang",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = product.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
                Text(
                    text = "Stok saat ini: ${product.stock} ${product.unit} (Min. Alert: ${product.minStock})",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "Pilih Cepat Tambah Stok:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    quickOptions.forEach { qty ->
                        val isSelected = addQtyStr == qty.toString()
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) RedPrimary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { addQtyStr = qty.toString() }
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                Text(
                                    text = "+$qty",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = addQtyStr,
                    onValueChange = { addQtyStr = it.filter { c -> c.isDigit() } },
                    label = { Text("Jumlah Tambahan (${product.unit})") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("restock_qty_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val qty = addQtyStr.toIntOrNull() ?: 0
                    if (qty > 0) {
                        onConfirmRestock(qty)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = RedPrimary),
                modifier = Modifier.testTag("confirm_restock_btn")
            ) {
                Text("Tambah Stok", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_restock_btn")
            ) {
                Text("Batal")
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}
