package com.unipi.george.chordshub.navigation.main

import android.annotation.SuppressLint
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.unipi.george.chordshub.R
import com.unipi.george.chordshub.navigation.AppScreens
import com.unipi.george.chordshub.repository.AuthRepository
import com.unipi.george.chordshub.repository.AuthRepository.fullNameState
import com.unipi.george.chordshub.repository.AuthRepository.isUserLoggedInState
import com.unipi.george.chordshub.screens.main.*
import com.unipi.george.chordshub.screens.viewsong.ArtistScreen
import com.unipi.george.chordshub.screens.viewsong.DetailedSongView
import com.unipi.george.chordshub.screens.slidemenu.viewprofile.EditProfileScreen
import com.unipi.george.chordshub.screens.viewsong.PlaylistDetailScreen
import com.unipi.george.chordshub.screens.slidemenu.ProfileMenu
import com.unipi.george.chordshub.screens.slidemenu.viewprofile.ProfileScreen
import com.unipi.george.chordshub.screens.auth.welcomeuser.WelcomeScreen
import com.unipi.george.chordshub.screens.slidemenu.RecentsScreen
import com.unipi.george.chordshub.screens.slidemenu.SettingsScreen
import com.unipi.george.chordshub.screens.slidemenu.UploadScreen
import com.unipi.george.chordshub.sharedpreferences.AppSettingsPreferences
import com.unipi.george.chordshub.viewmodels.main.HomeViewModel
import com.unipi.george.chordshub.viewmodels.MainViewModel
import com.unipi.george.chordshub.viewmodels.main.SearchViewModel
import com.unipi.george.chordshub.viewmodels.user.SessionViewModel
import com.unipi.george.chordshub.viewmodels.user.SettingsViewModel
import com.unipi.george.chordshub.viewmodels.user.UserViewModel

/*
 * Main navigation graph for the app.
 *
 * Defines the navigation structure between all main and secondary screens,
 * including Home, Search, Upload, Library, Settings, Profile, Recents,
 * as well as deep-linked screens like DetailedSongView, ArtistScreen, and PlaylistDetail.
 *
 * It handles:
 * - Full screen state management
 * - Menu visibility (ProfileMenu)
 * - Argument passing (e.g., artistName, playlistName)
 * - ViewModel instantiation and sharing across composables
 */


@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun MainNavGraph(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    sessionViewModel : SessionViewModel
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
        startDestination = AppScreens.Home.route
    ) {
        composable(AppScreens.Home.route) {
            HomeScreen(
                homeViewModel = homeViewModel,
                mainViewModel = mainViewModel,
                userViewModel = userViewModel,
                navController = navController,
                painter = painter,
                onMenuClick = { mainViewModel.setMenuOpen(true) }
            )
        }

        composable(AppScreens.Search.route) {
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
        composable(AppScreens.Upload.route) {
            UploadScreen(
                navController = navController,
                painter = painter,
                onMenuClick = { mainViewModel.setMenuOpen(true) }
            )
        }
        composable(AppScreens.Library.route) {
            LibraryScreen(
                navController = navController,
                painter = painter,
                mainViewModel = mainViewModel,
                onMenuClick = { mainViewModel.setMenuOpen(true) }
            )
        }
        composable(AppScreens.Profile.route) {
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

        composable(AppScreens.Settings.route) {
            SettingsScreen(navController = navController, settingsViewModel = settingsViewModel)
        }

        composable(AppScreens.Recents.route) {
            RecentsScreen(navController = navController, userViewModel = userViewModel, homeViewModel = homeViewModel)
        }

        composable("recentsScreen") {
            RecentsScreen(navController, userViewModel, homeViewModel)
        }

        composable(
            route = "detailedSongView/{songTitle}",
            arguments = listOf(navArgument("songTitle") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val songTitle = backStackEntry.arguments?.getString("songTitle")

            DetailedSongView(
                songId = songTitle ?: return@composable,
                isFullScreenState = false, // ή ό,τι έχεις default
                onBack = { navController.popBackStack() },
                navController = navController,
                mainViewModel = mainViewModel,
                homeViewModel = homeViewModel,
                userViewModel = userViewModel
            )
        }

        composable(AppScreens.Welcome.route) {
            WelcomeScreen(navController, sessionViewModel)
        }
        composable("playlist_detail/{playlistName}") { backStackEntry ->
            val name = backStackEntry.arguments?.getString("playlistName") ?: return@composable
            PlaylistDetailScreen(
                playlistName = name,
                onBack = { navController.popBackStack() },
                viewModel = viewModel()
            )
        }
    }

    ProfileMenu(mainViewModel, navController = navController)
}
