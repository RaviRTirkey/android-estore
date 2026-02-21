package com.learning.e_store.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learning.e_store.data.remote.api.model.Product
import com.learning.e_store.data.repository.CartRepository
import com.learning.e_store.data.repository.ProductRepository
import com.learning.e_store.data.repository.ProductResult
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle, // Used to retrieve navigation arguments
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val productId: Long = checkNotNull(savedStateHandle["productId"])

    private val _productState = MutableStateFlow(ProductDetailUiState())
    val productState = _productState.asStateFlow()

    init {
        loadProduct()
    }

    private fun loadProduct() {
        viewModelScope.launch {
            _productState.update { it.copy(isLoading = true) }
            
            val result = productRepository.getProductById(productId)
            
            when (result){
                is ProductResult.Success -> {
                    _productState.update { it.copy(product = result.product, isLoading = false) }
                }
                ProductResult.NoContent -> {
                    _productState.update { it.copy(product = null, isLoading = false) }
                }
                ProductResult.NetworkError -> {
                    _productState.update { it.copy(error = "Check your internet connection", isLoading = false) }
                }
                ProductResult.UnknownError -> {
                    _productState.update { it.copy(error = "Something went wrong", isLoading = false) }
                }
            }
            
        }
    }

    fun addToCart() {
        // Handle add to cart logic here
        println("Added product $productId to cart")
    }
}

data class ProductDetailUiState(
    val product: Product? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)