package com.example.codecraft.ui.theme.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.codecraft.ui.theme.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.codecraft.data.SessionManager
import com.example.codecraft.ui.home.HomeScreen
import com.example.codecraft.ui.lessons.LessonsScreen
import com.example.codecraft.ui.profile.ProfileScreen

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem("home_tab", Icons.Default.Home, "Главная")
    object Courses : BottomNavItem("courses_tab", Icons.Default.List, "Курсы")
    object Profile : BottomNavItem("profile_tab", Icons.Default.Person, "Профиль")
}

@Composable
fun MainScreen(
    rootNavController: NavController,
    sessionManager: SessionManager,
    onLogout: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val navController = rememberNavController()
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Courses,
        BottomNavItem.Profile
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Surface,
                contentColor = Accent
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Accent,
                            selectedTextColor = Accent,
                            unselectedIconColor = TextSecond,
                            unselectedTextColor = TextSecond,
                            indicatorColor = Accent.copy(alpha = 0.1f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Home.route) {
                HomeScreen(
                    navController = rootNavController,
                    sessionManager = sessionManager,
                    onLogout = onLogout
                )
            }
            composable(BottomNavItem.Courses.route) {
                LessonsScreen(
                    navController = rootNavController,
                    sessionManager = sessionManager,
                    showBackButton = false
                )
            }
            composable(BottomNavItem.Profile.route) {
                ProfileScreen(
                    sessionManager = sessionManager,
                    onLogout = onLogout,
                    onEditProfile = onNavigateToEditProfile,
                    onSettings = onNavigateToSettings
                )
            }
        }
    }
}
