package com.learning.e_store

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.learning.e_store.core.session.SessionEventManager
import com.learning.e_store.core.session.SessionManager
import com.learning.e_store.screen.navigation.AppNavigation
import com.learning.e_store.ui.theme.EStoreTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject lateinit var sessionEventManager: SessionEventManager
    @Inject lateinit var sessionManager: SessionManager
    
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            
            //observe Session expiration
            LaunchedEffect(sessionEventManager) {
                sessionEventManager.sessionExpired.collect {
                    if (navController.currentDestination?.route != "login") {
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            }
            EStoreTheme {
                AppNavigation(navController)
                
            }
        }
    }
}
