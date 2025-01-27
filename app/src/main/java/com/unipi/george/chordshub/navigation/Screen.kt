package com.unipi.george.chordshub.navigation



sealed class Screen(val route: String) {
    data object Home : Screen("Home")
    data object Profile : Screen("Profile")
    data object Settings : Screen("Settings")
    data object Login : Screen("Login")
    data object SignUp : Screen("SignUp")

}
