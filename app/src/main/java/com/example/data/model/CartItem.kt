package com.example.data.model

data class CartItem(
    val product: ProductEntity,
    val quantity: Int
) {
    val subtotal: Long get() = product.sellPrice * quantity
    val subtotalCost: Long get() = product.buyPrice * quantity
    val subtotalProfit: Long get() = (product.sellPrice - product.buyPrice) * quantity
}
