package com.learning.e_store.data.remote.api.model

import kotlinx.serialization.Serializable

@Serializable
data class OrderItemResponse(
    val productId: Long,
    val productName: String,
    val productImage: String,
    val quantity: Int,
    val priceAtPurchase: String
)
