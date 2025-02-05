package com.unipi.george.chordshub.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.unipi.george.chordshub.screens.HomeScreen
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.unipi.george.chordshub.repository.AuthRepository
import com.unipi.george.chordshub.screens.LibraryScreen
import com.unipi.george.chordshub.screens.LoginScreen
import com.unipi.george.chordshub.screens.SearchScreen
import com.unipi.george.chordshub.screens.SignUpScreen
import com.unipi.george.chordshub.viewmodels.HomeViewModel

@Composable
fun NavGraph(navController: NavHostController, homeViewModel: HomeViewModel) {
    val isUserLoggedInState = remember { mutableStateOf(AuthRepository.isUserLoggedIn()) }
    val isExpanded = remember { mutableStateOf(false) }
    NavHost(
        navController = navController,
        startDestination = if (isUserLoggedInState.value) Screen.Home.route else Screen.Login.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                navController = navController,
                homeViewModel = homeViewModel,
                isFullScreen = isExpanded.value,
                onFullScreenChange = { isExpanded.value = it }
            )
        }
        composable(Screen.Search.route) {
            SearchScreen(navController)
        }
        composable(Screen.Library.route) {
            LibraryScreen(navController)
        }
        composable(Screen.Login.route) {
            LoginScreen(
                navController = navController,
                onLoginSuccess = {
                    isUserLoggedInState.value = true
                    navController.navigate(Screen.Home.route)
                }
            )
        }
        composable(Screen.SignUp.route) {
            SignUpScreen(navController)
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        Screen.Home,
        Screen.Search,
        Screen.Library
    )

    NavigationBar(
        containerColor = Color.Transparent,
        tonalElevation = 0.dp
    ){
        items.forEach { screen ->
            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
            NavigationBarItem(
                label = { Text(screen.route.uppercase()) },
                icon = {
                    when (screen) {
                        is Screen.Home -> Text("🏠")
                        is Screen.Search -> Text("🔍")
                        is Screen.Library -> Text("📚")
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