package com.learning.e_store.screen.navigation

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.learning.e_store.screen.AuthRoute
import com.learning.e_store.screen.CartRoute
import com.learning.e_store.screen.CategoriesScreenRoute
import com.learning.e_store.screen.CategoryProductsScreenRoute
import com.learning.e_store.screen.CheckoutScreenRoute
import com.learning.e_store.screen.DetailScreenRoute
import com.learning.e_store.screen.HomeScreenRoute
import com.learning.e_store.screen.LoginRoute
import com.learning.e_store.screen.OrdersScreenRoute
import com.learning.e_store.screen.ProfileScreenRoute
import com.learning.e_store.screen.SignupRoute
import com.learning.e_store.screen.StartupRoute

@Composable
fun AppNavigation(
    navController: NavHostController
) {

    NavHost(navController = navController, startDestination = "startup") {
        
        composable("startup") {
            StartupRoute(navController = navController)
        }
        
        composable("auth") {
            AuthScaffold { _, _ ->
                AuthRoute(navController = navController)
            }
        }
        composable("login") {
            AuthScaffold { padding, snackbarHostState ->
                LoginRoute(
                    navController = navController,
                    snackbarHostState = snackbarHostState
                )
            }
        }
        composable("signup") {
            AuthScaffold { _, snackbarHostState ->
                SignupRoute(
                    navController = navController,
                    snackbarHostState = snackbarHostState
                )
            }
        }



        composable("home") {
            MainScaffold(navController = navController) { padding, snackbarHostState ->
                HomeScreenRoute(
                    contentPadding = padding,
                    navHostController = navController,
                    snackbarHostState = snackbarHostState
                )
            }
        }

        composable("categories") {
            MainScaffold(navController = navController) { padding, snackbarHostState ->
                CategoriesScreenRoute(
                    contentPadding = padding,
                    navController = navController,
                    snackbarHostState = snackbarHostState
                )
            }
        }

        composable("cart") {
            MainScaffold(navController = navController) { padding, snackbarHostState ->
                CartRoute(contentPadding = padding, navController = navController)
            }
        }

        composable("profile") {
            MainScaffold(navController = navController) { padding, snackbarHostState ->
                ProfileScreenRoute(contentPadding = padding, navController = navController)
            }
        }

        composable("detail/{productId}", arguments = listOf(navArgument("productId") {
            type = NavType.LongType
        })) {
            MainScaffold(navController = navController) { padding, snackbarHostState ->
                DetailScreenRoute(
                    navController = navController,
                    snackbarHostState = snackbarHostState
                )
            }
        }
        
        composable("checkout") {
            CheckoutScreenRoute(navController = navController)
        }
        
        composable("orders") {
            MainScaffold(navController = navController){ padding, snackbarHostState ->
                OrdersScreenRoute(navController = navController, contentPadding = padding, snackbarHostState = snackbarHostState)
            }
        }

        composable(
            route = "categoryProducts/{categoryId}",
            arguments = listOf(navArgument("categoryId"){
                type = NavType.LongType
            })
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getLong("categoryId")
            Log.d("CategoryProductsScreen", "categoryId: $categoryId")
            
            MainScaffold(navController = navController) { padding, snackbarHostState ->
                CategoryProductsScreenRoute(
                    categoryId = categoryId ?: 1L,
                    contentPadding = padding,
                    navHostController = navController,
                    snackbarHostState = snackbarHostState
                )
            }
        }
        


    }
}

@Composable
fun AuthScaffold(
    content: @Composable (PaddingValues, SnackbarHostState) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        content(padding, snackbarHostState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    navController: NavHostController,
    content: @Composable (PaddingValues, SnackbarHostState) -> Unit

) {
    val snackbarHostState = remember { SnackbarHostState() }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (currentRoute != "detail/{productId}") {
                MainBottomNavigation(navController)
            }
        },
        topBar = {
            if (currentRoute != "detail/{productId}") {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            stringResource(id = com.learning.e_store.R.string.app_name),
                            fontWeight = FontWeight.Bold
                        )
                    }
                )
            }
        }
    ) { padding ->
        content(padding, snackbarHostState)
    }
}