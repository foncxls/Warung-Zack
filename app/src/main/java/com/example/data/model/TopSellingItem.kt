package com.example.data.model

data class TopSellingItem(
    val productName: String,
    val category: String,
    val totalQuantitySold: Int,
    val totalRevenue: Long,
    val totalProfit: Long
)
