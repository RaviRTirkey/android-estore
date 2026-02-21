package com.learning.e_store.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.learning.e_store.viewmodel.AuthState
import com.learning.e_store.viewmodel.StartupViewModel

@Composable
fun StartupRoute(navController: NavHostController, viewModel: StartupViewModel = hiltViewModel()) {
    val state by viewModel.authState.collectAsState()
    
    when(state){
        AuthState.Loading -> {
//            SplashScreen()
        }
        AuthState.Authenticated -> LaunchedEffect(Unit) {
            navController.navigate("home") {
                popUpTo("startup") { inclusive = true }
            }
        }
        AuthState.Unauthenticated -> {
            LaunchedEffect(Unit) {
                navController.navigate("auth") {
                    popUpTo("startup") { inclusive = true }
                }
            }
        }
    }

}