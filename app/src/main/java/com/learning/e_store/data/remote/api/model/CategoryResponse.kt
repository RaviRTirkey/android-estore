package com.learning.e_store.data.remote.api.model

import kotlinx.serialization.Serializable

@Serializable
data class CategoryResponse(
    val id: Long,
    val name: String,
    val imageUrl: String?
)
