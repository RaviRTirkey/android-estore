package com.learning.e_store.data.remote.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Product(
    @SerialName("id")
    val productId: Long,
    @SerialName("name")
    val productName: String,
    @SerialName("description")
    val productDescription: String,
    @SerialName("price")
    val displayPrice: String,
    @SerialName("stockQuantity")
    val stockQuantity: Int,
    @SerialName("imageUrl")
    val imageUrl: String,
    @SerialName("categoryName")
    val category: String
)