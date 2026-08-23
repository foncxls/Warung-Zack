package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.ProductEntity
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionItemEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ProductEntity::class,
        TransactionEntity::class,
        TransactionItemEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "warung_zack_db"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.productDao())
                    }
                }
            }
        }

        suspend fun populateInitialData(productDao: ProductDao) {
            if (productDao.getProductsCount() == 0) {
                val initialProducts = listOf(
                    ProductEntity(
                        name = "Beras Premium 5kg",
                        category = "Sembako",
                        buyPrice = 65000,
                        sellPrice = 72000,
                        stock = 12,
                        minStock = 5,
                        unit = "Sak"
                    ),
                    ProductEntity(
                        name = "Minyak Goreng 2L",
                        category = "Sembako",
                        buyPrice = 32000,
                        sellPrice = 36000,
                        stock = 8,
                        minStock = 5,
                        unit = "Pouch"
                    ),
                    ProductEntity(
                        name = "Gula Pasir 1kg",
                        category = "Sembako",
                        buyPrice = 15000,
                        sellPrice = 17500,
                        stock = 4, // Low stock demo
                        minStock = 6,
                        unit = "Kg"
                    ),
                    ProductEntity(
                        name = "Telur Ayam 1kg",
                        category = "Sembako",
                        buyPrice = 26000,
                        sellPrice = 29000,
                        stock = 15,
                        minStock = 5,
                        unit = "Kg"
                    ),
                    ProductEntity(
                        name = "Indomie Goreng",
                        category = "Makanan/Snack",
                        buyPrice = 2800,
                        sellPrice = 3500,
                        stock = 40,
                        minStock = 10,
                        unit = "Bks"
                    ),
                    ProductEntity(
                        name = "Indomie Kuah Kari",
                        category = "Makanan/Snack",
                        buyPrice = 2800,
                        sellPrice = 3500,
                        stock = 3, // Low stock demo
                        minStock = 10,
                        unit = "Bks"
                    ),
                    ProductEntity(
                        name = "Kopi Kapal Api Mix",
                        category = "Minuman",
                        buyPrice = 1500,
                        sellPrice = 2000,
                        stock = 50,
                        minStock = 12,
                        unit = "Bks"
                    ),
                    ProductEntity(
                        name = "Teh Celup Sariwangi",
                        category = "Minuman",
                        buyPrice = 5500,
                        sellPrice = 7000,
                        stock = 0, // Out of stock demo
                        minStock = 5,
                        unit = "Kotak"
                    ),
                    ProductEntity(
                        name = "Air Mineral 600ml",
                        category = "Minuman",
                        buyPrice = 2500,
                        sellPrice = 3500,
                        stock = 24,
                        minStock = 8,
                        unit = "Btl"
                    ),
                    ProductEntity(
                        name = "Sabun Mandi Lifebuoy",
                        category = "Kebersihan",
                        buyPrice = 3500,
                        sellPrice = 4500,
                        stock = 18,
                        minStock = 6,
                        unit = "Pcs"
                    ),
                    ProductEntity(
                        name = "Sunlight Cuci Piring",
                        category = "Kebersihan",
                        buyPrice = 9000,
                        sellPrice = 11000,
                        stock = 2, // Low stock demo
                        minStock = 5,
                        unit = "Pouch"
                    ),
                    ProductEntity(
                        name = "Deterjen Rinso 770g",
                        category = "Kebersihan",
                        buyPrice = 18000,
                        sellPrice = 21000,
                        stock = 7,
                        minStock = 4,
                        unit = "Bks"
                    ),
                    ProductEntity(
                        name = "Rokok Surya 16",
                        category = "Rokok",
                        buyPrice = 31000,
                        sellPrice = 34000,
                        stock = 10,
                        minStock = 5,
                        unit = "Bks"
                    ),
                    ProductEntity(
                        name = "Biskuit Roma Kelapa",
                        category = "Makanan/Snack",
                        buyPrice = 8500,
                        sellPrice = 10500,
                        stock = 9,
                        minStock = 4,
                        unit = "Bks"
                    )
                )
                productDao.insertProducts(initialProducts)
            }
        }
    }
}
