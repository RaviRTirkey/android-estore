package com.learning.e_store.data.repository

import android.util.Log
import android.util.Log.e
import com.learning.e_store.data.remote.api.OrderApi
import com.learning.e_store.data.remote.api.model.Address
import com.learning.e_store.data.remote.api.model.Cart
import com.learning.e_store.data.remote.api.model.OrderResponse
import java.io.IOException
import javax.inject.Inject

class OrderRepository @Inject constructor(
    private val orderApi: OrderApi
) {
    suspend fun placeOrder(address: Address): OrderResult {
        return try {
            val response = orderApi.placeOrder(address)
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.items.isNullOrEmpty()){
                    Log.d("OrderRepository", "placeOrder: ${body}")
                    OrderResult.NoContent
                }else{
                    Log.d("OrderRepository", "placeOrder: ${body}")
                    OrderResult.Success(body)
                }
                
            } else{
                Log.d("OrderRepository", "placeOrder: ${response.code()}")
                OrderResult.UnknownError
            }
                
        } catch (e: IOException) {
            Log.d("OrderRepository", "placeOrder: ${e}")
            OrderResult.NetworkError
        } catch (e: Exception) {
            Log.d("OrderRepository", "placeOrder: ${e}")
            OrderResult.UnknownError
        }
    }
    
    suspend fun getMyOrders(): OrderListResult {
        return try{
            val response = orderApi.getOrders()

            if (response.isSuccessful) {
                val body = response.body()
                if (body == null) {
                    Log.d("OrderRepository", "getMyOrder: ${body}")
                    OrderListResult.NoContent
                } else {
                    Log.d("OrderRepository", "getMyOrder: ${body}")
                    OrderListResult.Success(body)
                }

            } else {
                Log.d("OrderRepository", "getMyOrder: ${response.code()}")
                OrderListResult.UnknownError
            }

        } catch (e: IOException) {
            Log.d("OrderRepository", "getMyOrder: ${e}")
            OrderListResult.NetworkError
        } catch (e: Exception) {
            Log.d("OrderRepository", "getMyOrder: ${e}")
            OrderListResult.UnknownError
        }
    }
}

sealed class OrderResult{
    data class Success(val orderResponse: OrderResponse): OrderResult()
    object NoContent: OrderResult()
    object NetworkError: OrderResult()
    object UnknownError: OrderResult()
}
sealed class OrderListResult{
    data class Success(val orderListResponse: List<OrderResponse>): OrderListResult()
    object NoContent: OrderListResult()
    object NetworkError: OrderListResult()
    object UnknownError: OrderListResult()
}
