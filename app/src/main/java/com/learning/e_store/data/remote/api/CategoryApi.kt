package com.learning.e_store.data.remote.api

import com.learning.e_store.data.remote.api.model.CategoryResponse
import retrofit2.Response
import retrofit2.http.GET


interface CategoryApi{
    @GET("api/v1/categories")
    suspend fun getCategories(): Response<List<CategoryResponse>>
}
