package com.learning.e_store.data.remote.api.model

import kotlinx.serialization.Serializable

@Serializable
data class PageResponse<T>(
    val content:List<T> = emptyList(),
    val totalElements: Long = 0,
    val totalPages: Int = 0,
    val number:Int = 0,
    val size:Int = 0,
    val first: Boolean = false,
    val last: Boolean = false
)
