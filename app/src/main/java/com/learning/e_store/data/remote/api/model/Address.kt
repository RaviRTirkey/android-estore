package com.learning.e_store.data.remote.api.model

import kotlinx.serialization.Serializable

@Serializable
data class Address(
    val name: String,
    val phone: String,
    val address: String,
    val state: String,
    val city: String,
    val pincode: String
)
