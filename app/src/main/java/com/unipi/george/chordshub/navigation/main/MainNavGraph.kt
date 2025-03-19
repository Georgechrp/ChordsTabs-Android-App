package com.unipi.george.chordshub.navigation.main

import android.annotation.SuppressLint
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.unipi.george.chordshub.R
import com.unipi.george.chordshub.navigation.Screen
import com.unipi.george.chordshub.repository.AuthRepository
import com.unipi.george.chordshub.repository.AuthRepository.fullNameState
import com.unipi.george.chordshub.repository.AuthRepository.isUserLoggedInState
import com.unipi.george.chordshub.screens.main.*
import com.unipi.george.chordshub.screens.seconds.ArtistScreen
import com.unipi.george.chordshub.screens.seconds.DetailedSongView
import com.unipi.george.chordshub.screens.seconds.EditProfileScreen
import com.unipi.george.chordshub.screens.seconds.ProfileMenu
import com.unipi.george.chordshub.screens.seconds.ProfileScreen
import com.unipi.george.chordshub.screens.slidemenu.RecentsScreen
import com.unipi.george.chordshub.screens.slidemenu.SettingsScreen
import com.unipi.george.chordshub.screens.slidemenu.UploadScreen
import com.unipi.george.chordshub.sharedpreferences.AppSettingsPreferences
import com.unipi.george.chordshub.viewmodels.main.HomeViewModel
import com.unipi.george.chordshub.viewmodels.MainViewModel
import com.unipi.george.chordshub.viewmodels.main.SearchViewModel
import com.unipi.george.chordshub.viewmodels.user.SettingsViewModel
import com.unipi.george.chordshub.viewmodels.user.UserViewModel

import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun MainNavGraph(
    navController: NavHostController,
    mainViewModel: MainViewModel
) {
    val homeViewModel: HomeViewModel = viewModel()
    val searchViewModel: SearchViewModel = viewModel()
    val userViewModel: UserViewModel = viewModel()
    val appSettingsPreferences = AppSettingsPreferences(navController.context)
    val settingsViewModel = SettingsViewModel(appSettingsPreferences)
    val isMenuOpen by mainViewModel.isMenuOpen
    val painter = painterResource(id = R.drawable.user_icon)
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    // Κλείσιμο του μενού όταν αλλάζει το BackStack
    LaunchedEffect(navBackStackEntry) {
        if (isMenuOpen) {
            mainViewModel.setMenuOpen(false)
        }
    }
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                homeViewModel = homeViewModel,
                mainViewModel = mainViewModel,
                userViewModel = userViewModel,
                navController = navController,
                painter = painter,
                onMenuClick = { mainViewModel.setMenuOpen(true) }
            )
        }

        composable(Screen.Search.route) {
            SearchScreen(
                viewModel = searchViewModel,
                mainViewModel = mainViewModel,
                homeViewModel = homeViewModel,
                painter = painter,
                onMenuClick = { mainViewModel.setMenuOpen(true) },
                navController = navController,
                isFullScreen = homeViewModel.isFullScreen.value,
                onFullScreenChange = { homeViewModel.setFullScreen(it) }
            )
        }
        composable(Screen.Upload.route) {
            UploadScreen(
                navController = navController,
                painter = painter,
                onMenuClick = { mainViewModel.setMenuOpen(true) }
            )
        }
        composable(Screen.Library.route) {
            LibraryScreen(
                navController = navController,
                painter = painter,
                mainViewModel = mainViewModel,
                onMenuClick = { mainViewModel.setMenuOpen(true) }
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                onLogout = {
                    AuthRepository.logoutUser()
                    isUserLoggedInState.value = false
                    fullNameState.value = null
                }
            )
        }

        composable("artist/{artistName}") { backStackEntry ->
            val artistName = backStackEntry.arguments?.getString("artistName") ?: "Άγνωστος Καλλιτέχνης"
            ArtistScreen(artistName = artistName, navController = navController)
        }

        composable("edit_profile/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
            EditProfileScreen(navController, userId, onDismiss = { navController.popBackStack() })
        }

        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController, settingsViewModel = settingsViewModel)
        }

        composable(Screen.Recents.route) {
            RecentsScreen(navController = navController, userViewModel = userViewModel)
        }

        composable("recentsScreen") {
            RecentsScreen(navController, userViewModel)
        }

        composable("detailedSongView/{songTitle}") { backStackEntry ->
            val songTitle = backStackEntry.arguments?.getString("songTitle")?.let {
                URLDecoder.decode(it, StandardCharsets.UTF_8.toString())
            } ?: "Untitled"

            DetailedSongView(
                songId = songTitle,
                isFullScreenState = false,
                onBack = { navController.popBackStack() },
                navController = navController,
                mainViewModel = viewModel(), // Χρησιμοποίησε `viewModel()` αντί για `MainViewModel()`
                homeViewModel = viewModel(),
                userViewModel = viewModel()
            )
        }
    }

    ProfileMenu(mainViewModel, navController = navController)
}
