package com.unipi.george.chordshub.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.unipi.george.chordshub.screens.HomeScreen
import com.unipi.george.chordshub.screens.ProfileScreen
import com.unipi.george.chordshub.screens.SettingsScreen
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem

import androidx.compose.material3.Text
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.unipi.george.chordshub.screens.LoginScreen
import com.unipi.george.chordshub.screens.SignUpScreen


@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController) // Βεβαιωθείτε ότι αυτή η συνάρτηση έχει @Composable
        }
        composable(Screen.Profile.route) {
            ProfileScreen() // Βεβαιωθείτε ότι αυτή η συνάρτηση έχει @Composable
        }
        composable(Screen.Settings.route) {
            SettingsScreen() // Βεβαιωθείτε ότι αυτή η συνάρτηση έχει @Composable
        }
        composable("Login") {
            LoginScreen(navController)
        }
        composable("SignUp") {
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
    val currentRoute = navController.currentBackStackEntryAsState()?.value?.destination?.route


}



/*@Composable
fun BottomNavigationBar(navController: NavHostController) {
    NavigationBar {
        NavigationBarItem(
            label = { Text("Home") },
            selected = true, // Για το παράδειγμα, το αφήνουμε σταθερό
            onClick = {
                navController.navigate("home")
            }
        )
    }
}*/



