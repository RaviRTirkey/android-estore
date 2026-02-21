package com.learning.e_store.data.remote.api

import com.learning.e_store.data.remote.api.model.PageResponse
import com.learning.e_store.data.remote.api.model.Product
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.math.BigDecimal

interface ProductApi {
    @GET("api/v1/products")
    suspend fun getProducts(
        @Query("name") name: String = "",
        @Query("minPrice") minPrice: BigDecimal = BigDecimal.ZERO,
        @Query("maxPrice") maxPrice: BigDecimal = BigDecimal(1_000_000),
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sortBy") sortBy: String = "id"
    ): Response<PageResponse<Product>>
    
    @GET("api/v1/products/{id}")
    suspend fun getProduct(
        @Path("id") id: Long
    ): Response<Product>

    @GET("api/v1/products/categoryId/{id}")
    suspend fun getProductsByCategory(
        @Path("id") id: Long,
        @Query("name") name: String = "",
        @Query("minPrice") minPrice: BigDecimal = BigDecimal.ZERO,
        @Query("maxPrice") maxPrice: BigDecimal = BigDecimal(1_000_000),
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sortBy") sortBy: String = "id"
    ): Response<PageResponse<Product>>
    
}