package com.unipi.george.chordshub.navigation

sealed class AppScreens(val route: String) {

    // ---------- Main Sections ----------
    data object Home : AppScreens("Home")
    data object Search : AppScreens("Search")
    data object Upload : AppScreens("Upload")
    data object Library : AppScreens("Library")
    data object Profile : AppScreens("Profile")

    // ---------- Auth ----------
    data object Login : AppScreens("Login")
    data object SignUp : AppScreens("SignUp")
    data object ForgotPassword : AppScreens("ForgotPassword")
    data object Welcome : AppScreens("welcome")

    // ---------- Navigation Roots ----------
    data object Auth : AppScreens("auth")
    data object Main : AppScreens("main")

    // ---------- Extras ----------
    data object Settings : AppScreens("Settings")
    data object Recents : AppScreens("Recents")
}
