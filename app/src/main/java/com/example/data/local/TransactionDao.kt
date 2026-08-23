package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionItemEntity
import com.example.data.model.TransactionWithItems
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Transaction
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactionsWithItems(): Flow<List<TransactionWithItems>>

    @Transaction
    @Query("SELECT * FROM transactions WHERE timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
    fun getTransactionsWithItemsBetween(startTime: Long, endTime: Long): Flow<List<TransactionWithItems>>

    @Query("SELECT * FROM transaction_items WHERE transactionId = :transactionId")
    suspend fun getItemsForTransaction(transactionId: Long): List<TransactionItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactionItems(items: List<TransactionItemEntity>)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)
}
