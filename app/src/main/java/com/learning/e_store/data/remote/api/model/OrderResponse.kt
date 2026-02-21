package com.learning.e_store.data.remote.api.model

import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDateTime

@Serializable
data class OrderResponse(
    val id: Long,
    val orderDate: LocalDateTime,
    val totalAmount: Double,
    val status: String,
    val items: List<OrderItemResponse>
)
