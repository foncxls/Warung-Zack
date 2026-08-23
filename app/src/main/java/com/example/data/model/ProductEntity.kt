package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val category: String, // Sembako, Minuman, Camilan/Makanan, Rokok, Kebersihan, Lainnya
    val buyPrice: Long, // Harga Beli (Modal)
    val sellPrice: Long, // Harga Jual
    val stock: Int, // Jumlah Stok Saat Ini
    val minStock: Int = 5, // Batas Minimum Stok Menipis
    val unit: String = "Pcs", // Pcs, Kg, Bks, Btl, Renteng, Dus
    val lastUpdated: Long = System.currentTimeMillis()
) {
    val isOutOfStock: Boolean get() = stock <= 0
    val isLowStock: Boolean get() = stock > 0 && stock <= minStock
    val isCritical: Boolean get() = stock <= minStock
    val profitPerUnit: Long get() = sellPrice - buyPrice
}
