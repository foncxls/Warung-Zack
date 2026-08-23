package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transaction_items",
    foreignKeys = [
        ForeignKey(
            entity = TransactionEntity::class,
            parentColumns = ["id"],
            childColumns = ["transactionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["transactionId"]),
        Index(value = ["productId"])
    ]
)
data class TransactionItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val transactionId: Long,
    val productId: Int,
    val productName: String,
    val category: String,
    val buyPrice: Long,
    val sellPrice: Long,
    val quantity: Int,
    val subtotal: Long
) {
    val totalProfit: Long get() = (sellPrice - buyPrice) * quantity
}
