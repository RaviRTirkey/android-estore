package com.learning.e_store.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learning.e_store.data.remote.api.model.Address
import com.learning.e_store.data.repository.OrderRepository
import com.learning.e_store.data.repository.OrderResult
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Holds the form data
data class CheckoutState(
    val name: String = "",
    val phone: String = "",
    val address: String = "",
    val state: String = "",
    val city: String = "",
    val pincode: String = "",

    val nameError: String? = null,
    val phoneError: String? = null,
    val addressError: String? = null,
    val stateError: String? = null,
    val cityError: String? = null,
    val pincodeError: String? = null,

    val isOrderPlaced: Boolean = false,
    val isLoading: Boolean = false
) {
    val isFormValid: Boolean
        get() = nameError == null &&
                phoneError == null &&
                addressError == null &&
                stateError == null &&
                cityError == null &&
                pincodeError == null &&

                name.isNotBlank() &&
                phone.isNotBlank() &&
                address.isNotBlank() &&
                state.isNotBlank() &&
                city.isNotBlank() &&
                pincode.isNotBlank()
}

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(CheckoutState())
    val uiState = _uiState.asStateFlow()

    // Generic function to update any field
    fun onNameChange(value: String) = _uiState.update {
        it.copy(
            name = value,
            nameError = if (value.isBlank()) "Name is required" else null
        )
    }

    fun onPhoneChange(value: String) {
        val error = when {
            value.isBlank() -> "Phone number is required"
            !value.matches(Regex("^[6-9]\\d{9}$")) -> "Enter valid 10-digit mobile number"
            else -> null
        }

        _uiState.update {
            it.copy(phone = value, phoneError = error)
        }
    }

    fun onAddressChange(newValue: String) = _uiState.update {
        it.copy(
            address = newValue,
            addressError = if (newValue.isBlank()) "Address is required" else null
        )
    }

    fun onStateChange(newValue: String) = _uiState.update {
        it.copy(
            state = newValue,
            stateError = if (newValue.isBlank()) "State is required" else null
        )
    }

    fun onCityChange(newValue: String) = _uiState.update {
        it.copy(
            city = newValue,
            cityError = if (newValue.isBlank()) "City is required" else null
        )
    }

    fun onPincodeChange(value: String) {
        val error = when {
            value.isBlank() -> "Pincode required"
            !value.matches(Regex("^\\d{6}$")) -> "Enter valid 6-digit pincode"
            else -> null
        }

        _uiState.update {
            it.copy(pincode = value, pincodeError = error)
        }
    }


    fun placeOrder() {
        Log.d("CheckoutViewModel", "placeOrder called")
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = orderRepository.placeOrder(
                Address(
                    name = _uiState.value.name,
                    phone = _uiState.value.phone,
                    address = _uiState.value.address,
                    state = _uiState.value.state,
                    city = _uiState.value.city,
                    pincode = _uiState.value.pincode
                )
            )
            
            val resultValue = when(result){
                is OrderResult.Success -> {
                    "Order placed successfully"
                }
                is OrderResult.NoContent -> {
                    "No content"
                }
                is OrderResult.NetworkError -> {
                    "Network Error"
                }
                is OrderResult.UnknownError -> {
                    "Unknown Error"
                }
            }
            
            Log.d("CheckoutViewModel", "Order result: $resultValue")
            

            // I will generate event (in future) from here based on the result and then show snackbar on ui

            _uiState.update { it.copy(isLoading = false, isOrderPlaced = true) }
        }
    }
}