package com.learning.e_store.data.remote.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Cart(
    @SerialName("id")
    val cartId: Long,
    @SerialName("items")
    val cartItems: List<CartItem>,
    @SerialName("totalCartPrice")
    val totalCartPrice: String
)
