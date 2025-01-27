package com.unipi.george.chordshub.navigation

import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.unipi.george.chordshub.screens.HomeScreen
import com.unipi.george.chordshub.screens.ProfileScreen
import com.unipi.george.chordshub.screens.SettingsScreen
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem

import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.unipi.george.chordshub.repository.AuthRepository
import com.unipi.george.chordshub.screens.LoginScreen
import com.unipi.george.chordshub.screens.SignUpScreen

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavGraph(navController: NavHostController) {
    val isUserLoggedInState = remember { mutableStateOf(AuthRepository.isUserLoggedIn()) }

    NavHost(
        navController = navController,
        startDestination = if (isUserLoggedInState.value) Screen.Home.route else Screen.Login.route
    ) {
        // Home Screen
        composable(Screen.Home.route) {
            Log.d("NavHost", "Navigated to Home Screen")
            HomeScreen(navController)
        }

        // Profile Screen
        composable(Screen.Profile.route) {
            Log.d("NavHost", "Navigated to Profile Screen")
            ProfileScreen(navController)
        }

        // Settings Screen
        composable(Screen.Settings.route) {
            Log.d("Navigation", "Navigated to Settings")
            SettingsScreen(
                navController = navController,
                onLogout = {
                    Log.d("NavHost", "User logged out. Navigating to Login Screen.")
                    isUserLoggedInState.value = false
                    AuthRepository.logoutUser()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        // Login Screen
        composable(Screen.Login.route) {
            Log.d("Navigation", "Navigated to Login")
            LoginScreen(
                navController = navController,
                onLoginSuccess = {
                    isUserLoggedInState.value = true
                    navController.navigate(Screen.Home.route)
                }
            )
        }

        // SignUp Screen
        composable(Screen.SignUp.route) {
            Log.d("Navigation", "Navigated to Signup")
            SignUpScreen(navController)
        }


    }
}




@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        Screen.Home,
        Screen.Profile,
        Screen.Settings
    )

    NavigationBar {
        items.forEach { screen ->
            val currentRoute = navController.currentBackStackEntryAsState()?.value?.destination?.route
            NavigationBarItem(
                label = { Text(screen.route.uppercase()) },
                icon = {
                    when (screen) {
                        is Screen.Home -> Text("🏠")
                        is Screen.Profile -> Text("👤")
                        is Screen.Settings -> Text("⚙️")
                        else -> {}
                    }
                },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}


// Helper functions για τα transitions
fun slideInFromRight() = slideInHorizontally(
    initialOffsetX = { fullWidth -> fullWidth },
    animationSpec = tween(500)
)

fun slideOutToLeft() = slideOutHorizontally(
    targetOffsetX = { fullWidth -> -fullWidth },
    animationSpec = tween(500)
)

fun slideInFromLeft() = slideInHorizontally(
    initialOffsetX = { fullWidth -> -fullWidth },
    animationSpec = tween(500)
)

fun slideOutToRight() = slideOutHorizontally(
    targetOffsetX = { fullWidth -> fullWidth },
    animationSpec = tween(500)
)


