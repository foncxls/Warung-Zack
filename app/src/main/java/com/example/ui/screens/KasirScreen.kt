package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CartItem
import com.example.data.model.ProductEntity
import com.example.ui.components.DEFAULT_CATEGORIES
import com.example.ui.theme.RedPrimary
import com.example.ui.theme.StockGreen
import com.example.ui.theme.StockOrange
import com.example.ui.theme.StockRed
import com.example.util.FormatUtils

@Composable
fun KasirScreen(
    products: List<ProductEntity>,
    cartItems: List<CartItem>,
    totalAmount: Long,
    totalCartItemsCount: Int,
    searchQuery: String,
    selectedCategory: String,
    notificationAlert: String?,
    onDismissNotificationAlert: () -> Unit,
    onSearchChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onAddToCart: (ProductEntity) -> Unit,
    onOpenCart: () -> Unit,
    onDirectCheckout: () -> Unit
) {
    val categories = listOf("Semua") + DEFAULT_CATEGORIES

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Auto Notification Alert Banner (if stock just became low/out)
            AnimatedVisibility(
                visible = notificationAlert != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                if (notificationAlert != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = notificationAlert,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                            IconButton(
                                onClick = onDismissNotificationAlert,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    Icons.Default.Clear,
                                    contentDescription = "Tutup",
                                    tint = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("Cari barang di warung...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = RedPrimary)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Hapus")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("kasir_search_input")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Category Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categories.forEach { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { onCategoryChange(cat) },
                        label = { Text(cat, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = RedPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Product Grid
            if (products.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.ShoppingBag,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Text(
                            text = "Barang tidak ditemukan",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .testTag("kasir_product_grid"),
                    contentPadding = PaddingValues(bottom = if (cartItems.isNotEmpty()) 80.dp else 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(products, key = { it.id }) { product ->
                        val cartQty = cartItems.find { it.product.id == product.id }?.quantity ?: 0
                        ProductGridItem(
                            product = product,
                            cartQuantity = cartQty,
                            onAddToCart = { onAddToCart(product) }
                        )
                    }
                }
            }
        }

        // Floating Sticky Checkout Bar (at bottom above nav)
        AnimatedVisibility(
            visible = cartItems.isNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenCart() }
                    .testTag("floating_cart_bar")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: Cart item count & total price
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = RedPrimary,
                                    contentColor = Color.White
                                ) {
                                    Text(
                                        text = "$totalCartItemsCount",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(RedPrimary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.ShoppingCart,
                                    contentDescription = "Keranjang",
                                    tint = RedPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Column {
                            Text(
                                text = "Total ($totalCartItemsCount item)",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = FormatUtils.formatRupiah(totalAmount),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = RedPrimary
                            )
                        }
                    }

                    // Right: Bayar Button
                    Button(
                        onClick = onDirectCheckout,
                        colors = ButtonDefaults.buttonColors(containerColor = RedPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("floating_checkout_btn")
                    ) {
                        Icon(Icons.Default.PointOfSale, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.size(6.dp))
                        Text("Bayar", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ProductGridItem(
    product: ProductEntity,
    cartQuantity: Int,
    onAddToCart: () -> Unit
) {
    val isOut = product.isOutOfStock
    val isLow = product.isLowStock

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isOut) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isOut) { onAddToCart() }
            .testTag("product_card_${product.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            // Category & Stock Status Badge Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = product.category,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )

                // Stock Badge
                val badgeColor = when {
                    isOut -> StockRed
                    isLow -> StockOrange
                    else -> StockGreen
                }
                val badgeText = when {
                    isOut -> "Habis"
                    isLow -> "Sisa ${product.stock}"
                    else -> "${product.stock} ${product.unit}"
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(badgeColor.copy(alpha = 0.15f))
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Product Name
            Text(
                text = product.name,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = if (isOut) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Price & Add to Cart Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = FormatUtils.formatRupiah(product.sellPrice),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isOut) MaterialTheme.colorScheme.onSurfaceVariant else RedPrimary
                )

                // 1-Tap Action Pill
                if (cartQuantity > 0) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(RedPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$cartQuantity",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(if (isOut) Color.Gray.copy(alpha = 0.3f) else RedPrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Tambah",
                            tint = if (isOut) Color.Gray else RedPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
