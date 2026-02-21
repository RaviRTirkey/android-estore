package com.learning.e_store.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learning.e_store.data.remote.api.model.CategoryResponse
import com.learning.e_store.data.repository.CategoriesResult
import com.learning.e_store.data.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class CategoriesUiState(
    val isLoading: Boolean = false,
    val categories: List<CategoryResponse> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoriesUiState())
    val uiState = _uiState.asStateFlow()

    fun loadCategories() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val result = categoryRepository.getCategories()
            if (result is CategoriesResult.Success) {
                _uiState.update { it.copy(categories = result.categories, isLoading = false) }
            } else {
                _uiState.update { it.copy(error = "Failed to load categories", isLoading = false) }
            }
        }
    }
}