package com.learning.e_store.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learning.e_store.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthState {
    object Loading : AuthState()
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
}

@HiltViewModel
class StartupViewModel@Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState = _authState.asStateFlow()
    
    init {
        validate()
    }
    
    private fun validate(){
        viewModelScope.launch { 
            val isValid = authRepository.validateSavedToken()
            
            _authState.value = if (isValid){
                AuthState.Authenticated
            }else{
                AuthState.Unauthenticated
            }
        
        }
    }
    
    
}