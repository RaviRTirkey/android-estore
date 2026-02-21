package com.learning.e_store.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learning.e_store.data.remote.api.model.Product
import com.learning.e_store.data.repository.ProductListResult
import com.learning.e_store.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val isPaginating: Boolean = false,
    val isLastPage: Boolean = false,
    val page: Int = 0,
    val selectedCategoryId: Long? = null,
    val error: String? = null
)

@HiltViewModel
class ProductViewModel @Inject constructor(private val productRepository: ProductRepository) :
    ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // Keep track of the active fetch job
    private var fetchJob: kotlinx.coroutines.Job? = null

    init {
        loadProducts()
    }

    fun loadProducts() {
        resetAndFetch(newCategoryId = null)
    }

    fun loadProductsByCategory(categoryId: Long) {
        resetAndFetch(newCategoryId = categoryId)
    }
    
    fun onRefresh(){
        resetAndFetch(newCategoryId = uiState.value.selectedCategoryId)
    }
    
    private fun resetAndFetch(newCategoryId: Long?){
        //Cancel the previous fetch job
        fetchJob?.cancel()
        
        _uiState.update { 
            it.copy(
                isLoading = true,
                selectedCategoryId = newCategoryId,
                products = emptyList(),
                page = 0,
                isLastPage = false,
                error = null
            )
        }
        
        fetchJob = viewModelScope.launch { 
            fetchProducts(categoryId = newCategoryId, page = 0)
        }
    }

    fun loadNextPage() {
        val currentState = _uiState.value

        // Prevent duplicate calls if already loading or reached the end
        if (currentState.isLoading || currentState.isPaginating || currentState.isLastPage) return

        fetchJob = viewModelScope.launch { 
            fetchProducts(page = currentState.page + 1, categoryId = currentState.selectedCategoryId)
        }
        
    }

    private suspend fun fetchProducts(page: Int, categoryId: Long? = null) {
        
        val result = if (categoryId == null) {
            productRepository.getProducts(page = page)
        } else {
            productRepository.getProductsByCategory(categoryId, page = page)
        }

        _uiState.update { currentState ->
            when (result) {
                is ProductListResult.Success -> currentState.copy(
                    products = if (page == 0) result.products else currentState.products + result.products,
                    isLastPage = result.isLastPage,
                    page = page,
                    isLoading = false,
                    isPaginating = false
                )

                is ProductListResult.NoContent -> currentState.copy(
                    isLastPage = true,
                    isLoading = false,
                    isPaginating = false
                )

                else -> currentState.copy(
                    error = if (result is ProductListResult.NetworkError) "Check connection" else "Unknown error",
                    isLoading = false,
                    isPaginating = false
                )
            }

        }
    }
    
}