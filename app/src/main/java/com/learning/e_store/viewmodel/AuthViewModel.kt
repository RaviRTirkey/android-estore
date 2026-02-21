package com.learning.e_store.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learning.e_store.data.repository.AuthRepository
import com.learning.e_store.data.repository.AuthResult
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    private val _events = MutableSharedFlow<UiEvent>( extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value) }
    }
    
    fun onNameChange(value: String){
        _uiState.update { it.copy(name = value) }
    }

    fun login() {
        val current = _uiState.value
        if (current.email.isBlank()) {
            _uiState.update { it.copy(error = "Email Required") }
            return
        }

        if (current.password.isBlank()) {
            _uiState.update { it.copy(error = "Password Required") }
            return
        }


        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = authRepository.loginUser(current.email, current.password)) {
                AuthResult.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(UiEvent.NavigateToHome)
                    return@launch
                }

                AuthResult.InvalidCredentials -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Invalid email or password"
                        )
                    }
                }

                AuthResult.NetworkError -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(UiEvent.ShowSnackbar("No internet connection"))
                }

                AuthResult.UnknownError -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(UiEvent.ShowSnackbar("Something went wrong"))
                }
            }
            

        }
    }
    
    fun register(){
        val current = _uiState.value
        
        if (current.email.isBlank()){
            _uiState.update {it.copy(error = "Email needed")}
            return
        }
        if (current.password.isBlank()){
            _uiState.update { it.copy(error = "Password needed") }
            return
        }
        
        viewModelScope.launch { 
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            when(authRepository.registerUser(current.email, current.name, current.password)){
                AuthResult.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(UiEvent.NavigateToHome)
                    return@launch
                }
                AuthResult.InvalidCredentials -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Invalid email or password"
                        )
                    }
                }

                AuthResult.NetworkError -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(UiEvent.ShowSnackbar("No internet connection"))
                }

                AuthResult.UnknownError -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(UiEvent.ShowSnackbar("Something went wrong"))
                }
            }
        }
    }
    
    fun logout(){
        Log.d("AuthViewModel", "logout: ")
        viewModelScope.launch {
            authRepository.logout()
            _events.emit(UiEvent.NavigateToAuth)
        }
    }


}

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val name: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class UiEvent {
    object NavigateToHome : UiEvent()
    object NavigateToAuth : UiEvent()
    data class ShowSnackbar(val message: String) : UiEvent()
}