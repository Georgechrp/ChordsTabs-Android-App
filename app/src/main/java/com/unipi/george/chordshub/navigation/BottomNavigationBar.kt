package com.unipi.george.chordshub.navigation

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState


@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        Screen.Home,
        Screen.Search,
        Screen.Upload,
        Screen.Library,
        Screen.Profile
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
                        is Screen.Home -> Text("🏠")//Image(painter = painterResource(id = R.drawable.ic_home), contentDescription = "Home")
                        is Screen.Search -> Text("🔍")//Image(painter = painterResource(id = R.drawable.ic_search), contentDescription = "Search")
                        is Screen.Upload -> Text("📤")//Image(painter = painterResource(id = R.drawable.ic_upload), contentDescription = "Upload")
                        is Screen.Library -> Text("📚")//Image(painter = painterResource(id = R.drawable.ic_library), contentDescription = "Library")
                        is Screen.Profile -> Text("👤")//Image(painter = painterResource(id = R.drawable.ic_profile), contentDescription = "Profile")
                        else -> {}
                    }
                },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(screen.route) { inclusive = true } // 🔥 Διαγράφει και ξαναφορτώνει τη σελίδα
                        launchSingleTop = true
                        restoreState = false
                    }
                }
            )
        }
    }
}