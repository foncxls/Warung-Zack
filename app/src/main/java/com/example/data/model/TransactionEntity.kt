package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val transactionCode: String, // e.g. WZ-20260821-0001
    val timestamp: Long = System.currentTimeMillis(),
    val totalAmount: Long, // Total Harga Jual
    val totalCost: Long, // Total Modal
    val totalProfit: Long, // Keuntungan Bersih
    val cashPaid: Long, // Nominal Bayar Tunai
    val changeAmount: Long, // Uang Kembalian
    val paymentMethod: String = "TUNAI", // TUNAI, QRIS, TRANSFER
    val totalItemsCount: Int = 0
)
