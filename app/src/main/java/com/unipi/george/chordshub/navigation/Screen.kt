package com.unipi.george.chordshub.navigation



sealed class Screen(val route: String) {
    data object Home : Screen("Home")
    data object Search : Screen("Search")
    data object Library : Screen("Library")
    data object Login : Screen("Login")
    data object SignUp : Screen("SignUp")

}
