package com.learning.e_store.screen.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.learning.e_store.viewmodel.AuthViewModel
import com.learning.e_store.viewmodel.UiEvent

@Composable
fun NavigateAuth(
    viewModel: AuthViewModel,
    navController: NavHostController,
    snackbarHostState: SnackbarHostState
) {
    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                UiEvent.NavigateToHome -> {
                    navController.navigate("home") {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }

                UiEvent.NavigateToAuth -> {
                    navController.navigate("auth"){
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }

                is UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }
}