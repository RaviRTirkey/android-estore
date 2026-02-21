package com.learning.e_store.data.remote.api.model

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val status: Int,
    val message: String,
    val timestamp: String,
    val errors: String?
)