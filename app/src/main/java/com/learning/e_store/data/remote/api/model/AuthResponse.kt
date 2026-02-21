package com.learning.e_store.data.remote.api.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String
)
