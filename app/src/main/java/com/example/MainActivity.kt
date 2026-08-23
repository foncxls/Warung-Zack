package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AddEditProductDialog
import com.example.ui.components.CartBottomSheet
import com.example.ui.components.CheckoutDialog
import com.example.ui.components.ReceiptDialog
import com.example.ui.components.WarungBottomNav
import com.example.ui.components.WarungTopBar
import com.example.ui.screens.KasirScreen
import com.example.ui.screens.LaporanScreen
import com.example.ui.screens.NotifikasiScreen
import com.example.ui.screens.StokScreen
import com.example.ui.theme.WarungZackTheme
import com.example.ui.viewmodel.WarungTab
import com.example.ui.viewmodel.WarungViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: WarungViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()

            WarungZackTheme(darkTheme = isDarkMode) {
                WarungZackApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WarungZackApp(viewModel: WarungViewModel) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()

    val allProducts by viewModel.allProducts.collectAsStateWithLifecycle()
    val filteredProducts by viewModel.filteredProducts.collectAsStateWithLifecycle()
    val lowStockProducts by viewModel.lowStockProducts.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()

    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val totalCartAmount by viewModel.totalCartAmount.collectAsStateWithLifecycle()
    val totalCartItemsCount by viewModel.totalCartItemsCount.collectAsStateWithLifecycle()

    val completedReceipt by viewModel.completedReceipt.collectAsStateWithLifecycle()
    val notificationAlertMessage by viewModel.notificationAlertMessage.collectAsStateWithLifecycle()

    val reportPeriod by viewModel.reportPeriod.collectAsStateWithLifecycle()
    val transactionsForPeriod by viewModel.transactionsForPeriod.collectAsStateWithLifecycle()
    val topSellingItems by viewModel.topSellingItems.collectAsStateWithLifecycle()

    val showProductDialog by viewModel.showProductDialog.collectAsStateWithLifecycle()
    val editingProduct by viewModel.editingProduct.collectAsStateWithLifecycle()

    var showCartSheet by remember { mutableStateOf(false) }
    var showCheckoutDialog by remember { mutableStateOf(false) }

    val cartSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            WarungTopBar(
                isDarkMode = isDarkMode,
                onToggleDarkMode = { viewModel.toggleDarkMode() },
                lowStockCount = lowStockProducts.size,
                onNotificationClick = { viewModel.setTab(WarungTab.NOTIFIKASI) },
                currentTab = currentTab
            )
        },
        bottomBar = {
            WarungBottomNav(
                currentTab = currentTab,
                onTabSelected = { viewModel.setTab(it) },
                lowStockCount = lowStockProducts.size,
                cartItemCount = totalCartItemsCount
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                WarungTab.KASIR -> {
                    KasirScreen(
                        products = filteredProducts,
                        cartItems = cartItems,
                        totalAmount = totalCartAmount,
                        totalCartItemsCount = totalCartItemsCount,
                        searchQuery = searchQuery,
                        selectedCategory = selectedCategory,
                        notificationAlert = notificationAlertMessage,
                        onDismissNotificationAlert = { viewModel.dismissNotificationAlert() },
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        onCategoryChange = { viewModel.setCategoryFilter(it) },
                        onAddToCart = { viewModel.addToCart(it) },
                        onOpenCart = { showCartSheet = true },
                        onDirectCheckout = { showCheckoutDialog = true }
                    )
                }

                WarungTab.STOK -> {
                    StokScreen(
                        products = filteredProducts,
                        allProductsCount = allProducts.size,
                        searchQuery = searchQuery,
                        selectedCategory = selectedCategory,
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        onCategoryChange = { viewModel.setCategoryFilter(it) },
                        onOpenAddProduct = { viewModel.openAddProductDialog() },
                        onOpenEditProduct = { viewModel.openEditProductDialog(it) },
                        onAdjustStock = { product, delta -> viewModel.adjustStockDelta(product, delta) },
                        onDeleteProduct = { viewModel.deleteProduct(it) }
                    )
                }

                WarungTab.LAPORAN -> {
                    LaporanScreen(
                        currentPeriod = reportPeriod,
                        transactions = transactionsForPeriod,
                        topSellingItems = topSellingItems,
                        onPeriodSelected = { viewModel.setReportPeriod(it) },
                        onDeleteTransaction = { viewModel.deleteTransaction(it) }
                    )
                }

                WarungTab.NOTIFIKASI -> {
                    NotifikasiScreen(
                        lowStockProducts = lowStockProducts,
                        onQuickRestock = { product, qty -> viewModel.quickRestock(product, qty) }
                    )
                }
            }
        }
    }

    // Add or Edit Product Dialog
    if (showProductDialog) {
        AddEditProductDialog(
            product = editingProduct,
            onDismiss = { viewModel.closeProductDialog() },
            onSave = { id, name, category, buyPrice, sellPrice, stock, minStock, unit ->
                viewModel.saveProduct(id, name, category, buyPrice, sellPrice, stock, minStock, unit)
                viewModel.closeProductDialog()
            }
        )
    }

    // Cart Bottom Sheet
    if (showCartSheet) {
        CartBottomSheet(
            sheetState = cartSheetState,
            cartItems = cartItems,
            totalAmount = totalCartAmount,
            onDismiss = { showCartSheet = false },
            onAddToCart = { viewModel.addToCart(it) },
            onDecreaseCartItem = { viewModel.decreaseCartItem(it) },
            onRemoveFromCart = { viewModel.removeFromCart(it) },
            onClearCart = {
                viewModel.clearCart()
                showCartSheet = false
            },
            onProceedToCheckout = {
                coroutineScope.launch {
                    cartSheetState.hide()
                    showCartSheet = false
                    showCheckoutDialog = true
                }
            }
        )
    }

    // Checkout Dialog
    if (showCheckoutDialog) {
        CheckoutDialog(
            cartItems = cartItems,
            totalAmount = totalCartAmount,
            onDismiss = { showCheckoutDialog = false },
            onConfirmCheckout = { cashPaid, paymentMethod ->
                showCheckoutDialog = false
                viewModel.processCheckout(cashPaid, paymentMethod)
            }
        )
    }

    // Digital Receipt Dialog
    if (completedReceipt != null) {
        ReceiptDialog(
            transactionWithItems = completedReceipt!!,
            onDismiss = { viewModel.dismissReceipt() }
        )
    }
}
