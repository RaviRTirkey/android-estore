package com.learning.e_store.data.remote.api

import com.learning.e_store.data.remote.api.model.Cart
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface CartApi {
    @GET("api/v1/cart/get")
    suspend fun getCart(): Response<Cart>
    
    @POST("api/v1/cart/add")
    suspend fun addToCart(
        @Query("productId") productId: Long,
        @Query("quantity") quantity: Int = 1
    ): Response<Cart>
    
    @DELETE("api/v1/cart/remove/{productId}")
    suspend fun deleteCartItem(
        @Path("productId") id: Long
    )
    
    
    @PUT("api/v1/cart/update")
    suspend fun updateCart(
        @Query("productId") productId: Long,
        @Query("quantity") quantity: Int
    )
}