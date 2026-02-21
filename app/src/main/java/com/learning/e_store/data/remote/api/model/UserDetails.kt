package com.learning.e_store.data.remote.api.model

import kotlinx.serialization.Serializable

@Serializable
data class UserDetails(
    val name: String,
    val email: String,
    val profilePic: String = ""
)
