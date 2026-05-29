package com.example.codecraft.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.codecraft.data.SessionManager
import com.example.codecraft.data.db.AppDatabase
import com.example.codecraft.data.repository.UserRepository
import com.example.codecraft.ui.auth.AuthScreen
import com.example.codecraft.ui.auth.AuthViewModel
import com.example.codecraft.ui.home.HomeScreen

sealed class Screen(val route: String) {
    object Auth : Screen("auth")
    object Home : Screen("home")
}

@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val navController = rememberNavController()

    val db = remember { AppDatabase.getInstance(context) }
    val sessionManager = remember { SessionManager(context) }
    val userRepository = remember { UserRepository(db.userDao()) }

    val startDestination = if (sessionManager.isLoggedIn) Screen.Home.route else Screen.Auth.route

    NavHost(navController = navController, startDestination = startDestination) {

        composable(Screen.Auth.route) {
            val viewModel = remember {
                AuthViewModel(userRepository, sessionManager)
            }
            AuthScreen(
                viewModel = viewModel,
                onAuthSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                sessionManager = sessionManager,
                onLogout = {
                    sessionManager.logout()
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
