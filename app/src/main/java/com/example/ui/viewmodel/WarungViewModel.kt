package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.CartItem
import com.example.data.model.ProductEntity
import com.example.data.model.TopSellingItem
import com.example.data.model.TransactionWithItems
import com.example.data.repository.WarungRepository
import com.example.util.FormatUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class WarungTab(val title: String) {
    KASIR("Kasir"),
    STOK("Stok"),
    LAPORAN("Laporan"),
    NOTIFIKASI("Peringatan")
}

enum class ReportPeriod(val label: String) {
    HARI_INI("Hari Ini"),
    KEMARIN("Kemarin"),
    TUJUH_HARI("7 Hari"),
    BULAN_INI("Bulan Ini"),
    SEMUA("Semua")
}

class WarungViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: WarungRepository

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = WarungRepository(database.productDao(), database.transactionDao())
        viewModelScope.launch {
            repository.populateInitialDataIfEmpty()
        }
    }

    // UI Navigation & Theme
    private val _currentTab = MutableStateFlow(WarungTab.KASIR)
    val currentTab: StateFlow<WarungTab> = _currentTab.asStateFlow()

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun setTab(tab: WarungTab) {
        _currentTab.value = tab
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    // All Products & Filter
    val allProducts: StateFlow<List<ProductEntity>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lowStockProducts: StateFlow<List<ProductEntity>> = repository.lowStockProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Semua")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    val filteredProducts: StateFlow<List<ProductEntity>> = combine(
        allProducts,
        searchQuery,
        selectedCategory
    ) { products, query, category ->
        products.filter { product ->
            val matchesQuery = query.isBlank() || product.name.contains(query, ignoreCase = true) || product.category.contains(query, ignoreCase = true)
            val matchesCategory = category == "Semua" || product.category.equals(category, ignoreCase = true)
            matchesQuery && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategoryFilter(category: String) {
        _selectedCategory.value = category
    }

    // Product Management
    fun saveProduct(
        id: Int = 0,
        name: String,
        category: String,
        buyPrice: Long,
        sellPrice: Long,
        stock: Int,
        minStock: Int,
        unit: String
    ) {
        viewModelScope.launch {
            val product = ProductEntity(
                id = id,
                name = name.trim(),
                category = category.trim().ifBlank { "Lainnya" },
                buyPrice = buyPrice,
                sellPrice = sellPrice,
                stock = stock,
                minStock = if (minStock > 0) minStock else 5,
                unit = unit.trim().ifBlank { "Pcs" },
                lastUpdated = System.currentTimeMillis()
            )
            if (id == 0) {
                repository.insertProduct(product)
            } else {
                repository.updateProduct(product)
            }
        }
    }

    fun deleteProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.deleteProduct(product)
            // Remove from cart if present
            removeFromCart(product.id)
        }
    }

    fun adjustStockDelta(product: ProductEntity, delta: Int) {
        viewModelScope.launch {
            val newStock = (product.stock + delta).coerceAtLeast(0)
            repository.updateStock(product.id, newStock)
        }
    }

    fun quickRestock(product: ProductEntity, addQty: Int) {
        viewModelScope.launch {
            repository.quickRestock(product.id, addQty)
        }
    }

    // POS / Cart State
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    val totalCartAmount: StateFlow<Long> = cartItems.combine(allProducts) { cart, _ ->
        cart.sumOf { it.subtotal }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val totalCartItemsCount: StateFlow<Int> = cartItems.combine(allProducts) { cart, _ ->
        cart.sumOf { it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun addToCart(product: ProductEntity) {
        if (product.stock <= 0) return
        val currentList = _cartItems.value.toMutableList()
        val index = currentList.indexOfFirst { it.product.id == product.id }
        if (index >= 0) {
            val currentQty = currentList[index].quantity
            if (currentQty < product.stock) {
                currentList[index] = currentList[index].copy(quantity = currentQty + 1)
                _cartItems.value = currentList
            }
        } else {
            currentList.add(CartItem(product = product, quantity = 1))
            _cartItems.value = currentList
        }
    }

    fun decreaseCartItem(productId: Int) {
        val currentList = _cartItems.value.toMutableList()
        val index = currentList.indexOfFirst { it.product.id == productId }
        if (index >= 0) {
            val currentQty = currentList[index].quantity
            if (currentQty > 1) {
                currentList[index] = currentList[index].copy(quantity = currentQty - 1)
                _cartItems.value = currentList
            } else {
                currentList.removeAt(index)
                _cartItems.value = currentList
            }
        }
    }

    fun removeFromCart(productId: Int) {
        _cartItems.value = _cartItems.value.filterNot { it.product.id == productId }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    // Checkout & Receipt
    private val _completedReceipt = MutableStateFlow<TransactionWithItems?>(null)
    val completedReceipt: StateFlow<TransactionWithItems?> = _completedReceipt.asStateFlow()

    private val _notificationAlertMessage = MutableStateFlow<String?>(null)
    val notificationAlertMessage: StateFlow<String?> = _notificationAlertMessage.asStateFlow()

    fun dismissNotificationAlert() {
        _notificationAlertMessage.value = null
    }

    fun processCheckout(cashPaid: Long, paymentMethod: String = "TUNAI") {
        val currentCart = _cartItems.value
        if (currentCart.isEmpty()) return

        viewModelScope.launch {
            val receipt = repository.processCheckout(
                cartItems = currentCart,
                cashPaid = cashPaid,
                paymentMethod = paymentMethod
            )
            _completedReceipt.value = receipt
            _cartItems.value = emptyList()

            // Notification message check
            val outOfStockNames = mutableListOf<String>()
            val lowStockNames = mutableListOf<String>()
            for (item in currentCart) {
                val p = repository.getProductById(item.product.id)
                if (p != null) {
                    if (p.stock <= 0) {
                        outOfStockNames.add(p.name)
                    } else if (p.isLowStock) {
                        lowStockNames.add("${p.name} (sisa ${p.stock})")
                    }
                }
            }

            if (outOfStockNames.isNotEmpty()) {
                _notificationAlertMessage.value = "⚠️ Perhatian: Stok '${outOfStockNames.first()}' telah habis!"
            } else if (lowStockNames.isNotEmpty()) {
                _notificationAlertMessage.value = "⚠️ Perhatian: Stok '${lowStockNames.first()}' sudah menipis!"
            }
        }
    }

    fun dismissReceipt() {
        _completedReceipt.value = null
    }

    // Reports / Laporan Harian
    private val _reportPeriod = MutableStateFlow(ReportPeriod.HARI_INI)
    val reportPeriod: StateFlow<ReportPeriod> = _reportPeriod.asStateFlow()

    fun setReportPeriod(period: ReportPeriod) {
        _reportPeriod.value = period
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val transactionsForPeriod: StateFlow<List<TransactionWithItems>> = _reportPeriod.flatMapLatest { period ->
        when (period) {
            ReportPeriod.HARI_INI -> {
                val (start, end) = FormatUtils.getTodayStartAndEnd()
                repository.getTransactionsForDateRange(start, end)
            }
            ReportPeriod.KEMARIN -> {
                val (start, end) = FormatUtils.getYesterdayStartAndEnd()
                repository.getTransactionsForDateRange(start, end)
            }
            ReportPeriod.TUJUH_HARI -> {
                val (start, end) = FormatUtils.getLast7DaysStartAndEnd()
                repository.getTransactionsForDateRange(start, end)
            }
            ReportPeriod.BULAN_INI -> {
                val (start, end) = FormatUtils.getThisMonthStartAndEnd()
                repository.getTransactionsForDateRange(start, end)
            }
            ReportPeriod.SEMUA -> {
                repository.allTransactions
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val topSellingItems: StateFlow<List<TopSellingItem>> = repository.getTopSellingItems(transactionsForPeriod)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteTransaction(tx: TransactionWithItems, restoreStock: Boolean = true) {
        viewModelScope.launch {
            repository.deleteTransaction(tx, restoreStock)
        }
    }

    // Dialogs / Modals UI state
    var editingProduct = MutableStateFlow<ProductEntity?>(null)
        private set
    var showProductDialog = MutableStateFlow(false)
        private set

    fun openAddProductDialog() {
        editingProduct.value = null
        showProductDialog.value = true
    }

    fun openEditProductDialog(product: ProductEntity) {
        editingProduct.value = product
        showProductDialog.value = true
    }

    fun closeProductDialog() {
        showProductDialog.value = false
        editingProduct.value = null
    }
}
