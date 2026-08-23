package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE stock <= minStock ORDER BY stock ASC, name ASC")
    fun getLowStockProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :productId LIMIT 1")
    suspend fun getProductById(productId: Int): ProductEntity?

    @Query("SELECT COUNT(*) FROM products")
    suspend fun getProductsCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    @Query("UPDATE products SET stock = :newStock, lastUpdated = :timestamp WHERE id = :productId")
    suspend fun updateStock(productId: Int, newStock: Int, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE products SET stock = MAX(0, stock - :qty), lastUpdated = :timestamp WHERE id = :productId")
    suspend fun reduceStock(productId: Int, qty: Int, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE products SET stock = stock + :qty, lastUpdated = :timestamp WHERE id = :productId")
    suspend fun increaseStock(productId: Int, qty: Int, timestamp: Long = System.currentTimeMillis())
}
