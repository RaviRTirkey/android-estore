package com.learning.e_store.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learning.e_store.data.remote.api.model.OrderResponse
import com.learning.e_store.data.repository.OrderListResult
import com.learning.e_store.data.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel@Inject constructor(
    private val orderRepository: OrderRepository
): ViewModel() {  
    
    val _uiState = MutableStateFlow(OrdersUiState())
    val uiState = _uiState.asStateFlow()
    
    fun loadOrders(){
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = orderRepository.getMyOrders()
            
            val orders = when(result){
                is OrderListResult.Success -> mapOrders(result.orderListResponse)
                else -> emptyList()
            }
            
            _uiState.value = _uiState.value.copy(orders = orders, isLoading = false)
        }
    }
    
    fun mapOrders(orders: List<OrderResponse>): List<Order> {
        return orders.map { orderResponse ->
            Order(
                orderId = orderResponse.id,
                orderDate = orderResponse.orderDate.toString(),
                totalAmount = orderResponse.totalAmount,
                status = orderResponse.status,
                itemsCount = orderResponse.items.size
            )
        }
    }
}

data class OrdersUiState(
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = false
)

data class Order(
    val orderId: Long,
    val orderDate: String,
    val totalAmount: Double,
    val status: String,
    val itemsCount: Int
)