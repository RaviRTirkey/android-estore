package com.learning.e_store.data.remote.api

import com.learning.e_store.data.remote.api.model.Address
import com.learning.e_store.data.remote.api.model.OrderResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET

import retrofit2.http.POST

interface OrderApi {
    @POST("api/v1/orders/placeOrder")
    suspend fun placeOrder(
        @Body address: Address
    ): Response<OrderResponse>
    
    @GET("api/v1/orders/my-orders")
    suspend fun getOrders(): Response<List<OrderResponse>>
}

