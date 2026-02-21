package com.learning.e_store.data.remote.api.model

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val email: String,
    val name: String,
    val password: String
)
