package com.learning.e_store.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learning.e_store.data.remote.api.model.CartItem
import com.learning.e_store.data.repository.CartRepository
import com.learning.e_store.data.repository.CartResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CartUiState(
    val cartItems: List<CartItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class CartUiEvent {
    data class ShowSnackbar(val message: String) : CartUiEvent()
}

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository
): ViewModel() {
    
    private val _uiState = MutableStateFlow(CartUiState())
    val uiState = _uiState.asStateFlow()
    
    private val _uiEvent = MutableSharedFlow<CartUiEvent>(extraBufferCapacity = 1)
    val uiEvent = _uiEvent.asSharedFlow()
    
    init {
        getCart()
    }
    
    fun getCart(){
        viewModelScope.launch { 
            _uiState.value = CartUiState(isLoading = true)
            try {
                val cartResult = cartRepository.getCart()
                when(cartResult){
                    is CartResult.Success ->{
                        _uiState.update { it.copy(cartItems = cartResult.cart.cartItems, isLoading = false) }
                    }
                    CartResult.NoContent ->{
                        _uiState.update { it.copy(cartItems = emptyList(), isLoading = false)}
                    }
                    CartResult.NetworkError ->{
                        _uiState.update { it.copy(error = "Check your internet connection", isLoading = false) }
                    }
                    CartResult.UnknownError -> {
                        _uiState.update { it.copy(error = "Something went wrong", isLoading = false) }
                    }
                }
            } catch (e: Exception){
                _uiState.value = CartUiState(error = e.message)
            }
            _uiState.update { it.copy(isLoading = false) }
            
        }
    }
    
    fun addToCart(productId: Long?, quantity: Int){
        if (productId == null) {
            _uiEvent.tryEmit(CartUiEvent.ShowSnackbar("Product not found"))
            return
        }
        
        viewModelScope.launch {
            
            _uiState.value = CartUiState(isLoading = true)
            try {
                val cartResult = cartRepository.addItemToCart(productId!!, quantity)
                when(cartResult){
                    is CartResult.Success ->{
                        _uiState.update { it.copy(cartItems = cartResult.cart.cartItems, isLoading = false) }
                        _uiEvent.emit(CartUiEvent.ShowSnackbar("Added to cart"))
                    }
                    CartResult.NoContent ->{
                        _uiState.update { it.copy(cartItems = emptyList(), isLoading = false)}
                        _uiEvent.emit(CartUiEvent.ShowSnackbar("No Content"))
                    }
                    CartResult.NetworkError ->{
                        _uiState.update { it.copy(error = "Check your internet connection", isLoading = false) }
                        _uiEvent.emit(CartUiEvent.ShowSnackbar("Check your internet connection"))
                    }
                    CartResult.UnknownError -> {
                        _uiState.update { it.copy(error = "Something went wrong", isLoading = false) }
                        _uiEvent.emit(CartUiEvent.ShowSnackbar("Something went wrong"))
                    }
                }
            } catch (e: Exception){
                _uiState.value = CartUiState(error = e.message)
            }
            _uiState.update { it.copy(isLoading = false) }

        }
    }
    
    fun deleteItem(productId: Long){
        viewModelScope.launch{
            _uiState.value = CartUiState(isLoading = true)
            try {
                cartRepository.deleteCartItem(productId)
                getCart()
            } catch (e: Exception) {
                _uiState.value = CartUiState(error = e.message)
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun updateItemQuantity(productId: Long, productQuantity: Int){
        viewModelScope.launch { 
            _uiState.value = CartUiState(isLoading = true)
            try {
                cartRepository.updateCartItemQuantity(productId, productQuantity)
                getCart()
            } catch (e: Exception) {
                _uiState.value = CartUiState(error = e.message)
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }
    
    
}