package com.learning.e_store.screen.navigation

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.learning.e_store.R

@Composable
fun MainBottomNavigation(navController: NavHostController) {

    val navItems = listOf(
        NavItem("Home", "home", Icons.Filled.Home, 0),
        NavItem(
            "Categories",
            "categories",
            ImageVector.vectorResource(R.drawable.ic_categories),
            0
        ),
        NavItem("Cart", "cart", Icons.Filled.ShoppingCart, 0),
        NavItem("Profile", "profile", Icons.Filled.Person, 0)
    )

    NavigationBar(modifier = Modifier.wrapContentHeight()) {
        val currentDestination = navController.currentBackStackEntryAsState().value?.destination

        navItems.forEach { item ->
            NavigationBarItem(
                selected = currentDestination?.route == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo("home")
                        launchSingleTop = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(text = item.label) },

                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = Color.Gray,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color.Transparent
                )

            )
        }
    }
}