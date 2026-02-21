package com.learning.e_store.data.repository

import android.util.Log
import com.learning.e_store.data.remote.api.CartApi
import com.learning.e_store.data.remote.api.model.Cart
import com.learning.e_store.data.remote.api.model.CartItem
import com.learning.e_store.data.remote.api.model.Product
import java.io.IOException
import javax.inject.Inject

class CartRepository @Inject constructor(
    val cartApi: CartApi
) {
    
    suspend fun getCart(): CartResult {
        return try {
            val result = cartApi.getCart()
            if (result.isSuccessful){
                val body = result.body()
                if (body?.cartItems.isNullOrEmpty()){
                    CartResult.NoContent
                }else{
                    CartResult.Success(body)
                }
            } else {
                CartResult.UnknownError
            }

        }catch (e: IOException) {
            Log.e("cartRepository", "Network Failure: ${e.message}")
            CartResult.NetworkError
        } catch (e: Exception) {
            Log.e("cartRepository", "Unknown Error", e)
            CartResult.UnknownError
        }
    }
    
    suspend fun addItemToCart(productId: Long, quantity: Int): CartResult{
        return try {
            val result = cartApi.addToCart(productId, quantity)
            if (result.isSuccessful){
                val body = result.body()
                if (body?.cartItems.isNullOrEmpty()){
                    CartResult.NoContent
                }else{
                    CartResult.Success(body)
                }
            } else {
                CartResult.UnknownError
            }

        }catch (e: IOException) {
            Log.e("cartRepository", "Network Failure: ${e.message}")
            CartResult.NetworkError
        } catch (e: Exception) {
            Log.e("cartRepository", "Unknown Error", e)
            CartResult.UnknownError
        }
    }
    
    suspend fun deleteCartItem(productId: Long){
        cartApi.deleteCartItem(productId)
    }
    
    suspend fun updateCartItemQuantity(productId: Long, productQuantity: Int) {
        cartApi.updateCart(productId, productQuantity)
    }
    
}

sealed class CartResult{
    data class Success(val cart: Cart): CartResult()
    object NoContent: CartResult()
    object NetworkError: CartResult()
    object UnknownError: CartResult()
}