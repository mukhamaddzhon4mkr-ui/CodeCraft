package com.example.codecraft.ui.theme.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.codecraft.data.SessionManager
import com.example.codecraft.data.db.AppDatabase
import com.example.codecraft.data.repository.UserRepository
import com.example.codecraft.data.repository.ProgressRepository
import com.example.codecraft.ui.theme.auth.AuthScreen
import com.example.codecraft.ui.theme.auth.AuthViewModel
import com.example.codecraft.ui.theme.lessons.LessonsScreen
import com.example.codecraft.ui.theme.lessons.LessonDetailScreen
import com.example.codecraft.ui.theme.profile.EditProfileScreen
import com.example.codecraft.ui.theme.settings.SettingsScreen
import com.example.codecraft.ui.theme.main.MainScreen
import com.example.codecraft.ui.viewmodel.ViewModelFactory

sealed class Screen(val route: String) {
    object Auth : Screen("auth")
    object Home : Screen("home")
    object Lessons : Screen("lessons")
    object EditProfile : Screen("edit_profile")
    object Settings : Screen("settings")
    object LessonDetail : Screen("lesson/{lessonId}") {
        fun createRoute(lessonId: String) = "lesson/$lessonId"
    }
}

@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val navController = rememberNavController()

    val db = remember { AppDatabase.getInstance(context) }
    val sessionManager = remember { SessionManager(context) }
    val userRepository = remember { UserRepository(db.userDao()) }
    val progressRepository = remember { ProgressRepository(db.progressDao(), db.notificationDao(), db.userDao()) }
    val viewModelFactory = remember { ViewModelFactory(userRepository, progressRepository) }

    val startDestination = if (sessionManager.isLoggedIn) Screen.Home.route else Screen.Auth.route

    NavHost(navController = navController, startDestination = startDestination) {

        composable(Screen.Auth.route) {
            val viewModel: AuthViewModel = viewModel(
                factory = object : ViewModelProvider.Factory{
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return AuthViewModel(userRepository, sessionManager) as T
                    }
                }
            )
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
            MainScreen(
                rootNavController = navController,
                sessionManager = sessionManager,
                onLogout = {
                    sessionManager.logout()
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToEditProfile = {
                    navController.navigate(Screen.EditProfile.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                viewModelFactory = viewModelFactory
            )
        }
        composable(Screen.EditProfile.route) {
            EditProfileScreen(
                sessionManager = sessionManager,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Lessons.route) {
            LessonsScreen(
                navController = navController,
                sessionManager = sessionManager,
                onBack = { navController.popBackStack() },
                viewModelFactory = viewModelFactory
            )
        }

        composable(
            route = Screen.LessonDetail.route,
            arguments = listOf(androidx.navigation.navArgument("lessonId") { type = androidx.navigation.NavType.StringType })
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getString("lessonId")
            LessonDetailScreen(
                lessonId = lessonId,
                navController = navController,
                sessionManager = sessionManager,
                onBack = { navController.popBackStack() },
                viewModelFactory = viewModelFactory
            )
        }
    }
}
