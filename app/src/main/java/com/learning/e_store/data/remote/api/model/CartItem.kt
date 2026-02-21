package com.learning.e_store.data.remote.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartItem(
    val productId: Long,
    val productName: String,
    val productImage: String,
    @SerialName("price")
    val productPrice: String,
    val quantity: Int
)
