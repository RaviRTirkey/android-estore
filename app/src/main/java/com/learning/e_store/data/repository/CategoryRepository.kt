package com.learning.e_store.data.repository

import com.learning.e_store.data.remote.api.CategoryApi
import com.learning.e_store.data.remote.api.model.CategoryResponse
import javax.inject.Inject

class CategoryRepository@Inject constructor(
    private val categoryApi: CategoryApi
) {
    
    suspend fun getCategories(): CategoriesResult {
        val response = categoryApi.getCategories()

        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                return CategoriesResult.Success(body)
            }
        }
        return CategoriesResult.UnknownError
    }
    
}

sealed class CategoriesResult {
    data class Success(val categories: List<CategoryResponse>) : CategoriesResult()
    object UnknownError : CategoriesResult()
}