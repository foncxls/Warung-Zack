package com.example.data.repository

import com.example.data.local.ProductDao
import com.example.data.local.TransactionDao
import com.example.data.model.CartItem
import com.example.data.model.ProductEntity
import com.example.data.model.TopSellingItem
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionItemEntity
import com.example.data.model.TransactionWithItems
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WarungRepository(
    private val productDao: ProductDao,
    private val transactionDao: TransactionDao
) {
    val allProducts: Flow<List<ProductEntity>> = productDao.getAllProducts()
    val lowStockProducts: Flow<List<ProductEntity>> = productDao.getLowStockProducts()
    val allTransactions: Flow<List<TransactionWithItems>> = transactionDao.getAllTransactionsWithItems()

    suspend fun insertProduct(product: ProductEntity): Long = productDao.insertProduct(product)

    suspend fun getProductById(productId: Int): ProductEntity? = productDao.getProductById(productId)

    suspend fun updateProduct(product: ProductEntity) = productDao.updateProduct(product)

    suspend fun deleteProduct(product: ProductEntity) = productDao.deleteProduct(product)

    suspend fun updateStock(productId: Int, newStock: Int) = productDao.updateStock(productId, newStock)

    suspend fun quickRestock(productId: Int, addQty: Int) = productDao.increaseStock(productId, addQty)

    suspend fun populateInitialDataIfEmpty() {
        if (productDao.getProductsCount() == 0) {
            com.example.data.local.AppDatabase.populateInitialData(productDao)
        }
    }

    suspend fun processCheckout(
        cartItems: List<CartItem>,
        cashPaid: Long,
        paymentMethod: String = "TUNAI"
    ): TransactionWithItems {
        val totalAmount = cartItems.sumOf { it.subtotal }
        val totalCost = cartItems.sumOf { it.subtotalCost }
        val totalProfit = totalAmount - totalCost
        val changeAmount = if (cashPaid >= totalAmount) cashPaid - totalAmount else 0L
        val totalItemsCount = cartItems.sumOf { it.quantity }

        val timeFormat = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault())
        val txCode = "WZ-" + timeFormat.format(Date())

        val transactionEntity = TransactionEntity(
            transactionCode = txCode,
            timestamp = System.currentTimeMillis(),
            totalAmount = totalAmount,
            totalCost = totalCost,
            totalProfit = totalProfit,
            cashPaid = cashPaid,
            changeAmount = changeAmount,
            paymentMethod = paymentMethod,
            totalItemsCount = totalItemsCount
        )

        val txId = transactionDao.insertTransaction(transactionEntity)

        val itemEntities = cartItems.map { cartItem ->
            TransactionItemEntity(
                transactionId = txId,
                productId = cartItem.product.id,
                productName = cartItem.product.name,
                category = cartItem.product.category,
                buyPrice = cartItem.product.buyPrice,
                sellPrice = cartItem.product.sellPrice,
                quantity = cartItem.quantity,
                subtotal = cartItem.subtotal
            )
        }

        transactionDao.insertTransactionItems(itemEntities)

        // Decrease stock for each item
        for (item in cartItems) {
            productDao.reduceStock(item.product.id, item.quantity)
        }

        return TransactionWithItems(
            transaction = transactionEntity.copy(id = txId),
            items = itemEntities
        )
    }

    fun getTransactionsForDateRange(startTime: Long, endTime: Long): Flow<List<TransactionWithItems>> {
        return transactionDao.getTransactionsWithItemsBetween(startTime, endTime)
    }

    suspend fun deleteTransaction(txWithItems: TransactionWithItems, restoreStock: Boolean = true) {
        if (restoreStock) {
            for (item in txWithItems.items) {
                productDao.increaseStock(item.productId, item.quantity)
            }
        }
        transactionDao.deleteTransaction(txWithItems.transaction)
    }

    fun getTopSellingItems(transactionsFlow: Flow<List<TransactionWithItems>>): Flow<List<TopSellingItem>> {
        return transactionsFlow.map { transactions ->
            val map = mutableMapOf<String, MutableTopItem>()
            for (tx in transactions) {
                for (item in tx.items) {
                    val key = item.productName
                    val existing = map.getOrPut(key) {
                        MutableTopItem(
                            productName = item.productName,
                            category = item.category,
                            totalQty = 0,
                            totalRev = 0L,
                            totalProf = 0L
                        )
                    }
                    existing.totalQty += item.quantity
                    existing.totalRev += item.subtotal
                    existing.totalProf += item.totalProfit
                }
            }
            map.values.map {
                TopSellingItem(
                    productName = it.productName,
                    category = it.category,
                    totalQuantitySold = it.totalQty,
                    totalRevenue = it.totalRev,
                    totalProfit = it.totalProf
                )
            }.sortedByDescending { it.totalQuantitySold }
        }
    }

    private data class MutableTopItem(
        val productName: String,
        val category: String,
        var totalQty: Int,
        var totalRev: Long,
        var totalProf: Long
    )
}
