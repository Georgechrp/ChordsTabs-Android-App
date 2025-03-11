package com.unipi.george.chordshub.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("Home")
    data object Search : Screen("Search")
    data object Upload : Screen("Upload")
    data object Library : Screen("Library")
    data object Profile : Screen("Profile")

    data object Login : Screen("Login")
    data object SignUp : Screen("SignUp")

    data object Settings : Screen("Settings")
    data object Recents : Screen("Recents")

    data object ForgotPassword : Screen("ForgotPassword")
}
