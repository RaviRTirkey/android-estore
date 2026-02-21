package com.learning.e_store.data.repository

import android.util.Log
import com.learning.e_store.data.remote.api.ProductApi
import com.learning.e_store.data.remote.api.model.Product
import java.io.IOException
import java.math.BigDecimal
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

sealed class ProductListResult {
    data class Success(
        val products: List<Product>,
        val isLastPage: Boolean
    ) : ProductListResult()
    object NoContent: ProductListResult()
    object NetworkError: ProductListResult()
    object UnknownError: ProductListResult()
}

sealed class ProductResult{
    data class Success(val product: Product): ProductResult()
    object NoContent: ProductResult()
    object NetworkError: ProductResult()
    object UnknownError: ProductResult()
}

class ProductRepository @Inject constructor(
    val productApi: ProductApi
) {
    
    suspend fun getProducts(
        name: String = "",
        minPrice: BigDecimal = BigDecimal.ZERO,
        maxPrice: BigDecimal = BigDecimal(1_000_000),
        page: Int = 0,
        size: Int = 10,
        sortBy: String = "id"
    ): ProductListResult {
        Log.d("productRepository", "getProducts is called")
        return try {
            val response = productApi.getProducts(
                name, minPrice, maxPrice, page, size, sortBy
            )
            
            if (response.isSuccessful){
                val body = response.body()
                Log.d("productRepository", "$body")
                if (body == null || body.content.isEmpty()){
                    ProductListResult.NoContent
                }else {
                    ProductListResult.Success(body.content, body.last)
                }
                
            } else {
                ProductListResult.UnknownError
            }
        } catch (e: IOException) {
            Log.e("productRepository", "Network Failure: ${e.message}") 
            ProductListResult.NetworkError
        } catch (e: Exception) {
            Log.e("productRepository", "Unknown Error", e) 
            ProductListResult.UnknownError
        }
    }   

    
    suspend fun getProductById(id: Long): ProductResult {
        return try {
            val response = productApi.getProduct(id)
            if (response.isSuccessful){
                val body = response.body()
                if (body == null || body.productName.isEmpty()){
                    ProductResult.NoContent
                }else {
                    ProductResult.Success(body)
                }
                
            } else{
                ProductResult.UnknownError
            }
        } catch (e: IOException) {
            ProductResult.NetworkError
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            ProductResult.UnknownError
        }
    }


    suspend fun getProductsByCategory(
        categoryId: Long = 1L,
        name: String = "",
        minPrice: BigDecimal = BigDecimal.ZERO,
        maxPrice: BigDecimal = BigDecimal(1_000_000),
        page: Int = 0,
        size: Int = 10,
        sortBy: String = "id"
    ): ProductListResult {
        Log.d("productRepository", "getProductsByCategory is called")
        return try {
            val response = productApi.getProductsByCategory(
                categoryId, name, minPrice, maxPrice, page, size, sortBy
            )

            if (response.isSuccessful){
                val body = response.body()
                Log.d("productRepository", "$body")
                if (body == null || body.content.isEmpty()){
                    ProductListResult.NoContent
                }else {
                    ProductListResult.Success(body.content, body.last)
                }

            } else {
                ProductListResult.UnknownError
            }
        } catch (e: IOException) {
            Log.e("productRepository", "Network Failure: ${e.message}")
            ProductListResult.NetworkError
        } catch (e: Exception) {
            Log.e("productRepository", "Unknown Error", e)
            ProductListResult.UnknownError
        }
    }
}