package com.unipi.george.chordshub.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.unipi.george.chordshub.R
import com.unipi.george.chordshub.repository.AuthRepository
import com.unipi.george.chordshub.repository.AuthRepository.fullNameState
import com.unipi.george.chordshub.repository.AuthRepository.isUserLoggedInState
import com.unipi.george.chordshub.screens.main.*
import com.unipi.george.chordshub.screens.seconds.ArtistScreen
import com.unipi.george.chordshub.screens.seconds.ProfileMenu
import com.unipi.george.chordshub.viewmodels.HomeViewModel
import com.unipi.george.chordshub.viewmodels.MainViewModel
import com.unipi.george.chordshub.viewmodels.SearchViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    mainViewModel: MainViewModel
) {
    val homeViewModel: HomeViewModel = viewModel()
    val searchViewModel: SearchViewModel = viewModel()
    val isMenuOpen = remember { mutableStateOf(false) }

    val painter = painterResource(id = R.drawable.user_icon)

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                isFullScreen = mainViewModel.isFullScreen.value,
                onFullScreenChange = { mainViewModel.setFullScreen(it) },
                homeViewModel = homeViewModel,
                navController = navController,
                painter = painter,
                onMenuClick = { isMenuOpen.value = true }
            )
        }

        composable(Screen.Search.route) {
            SearchScreen(
                viewModel = searchViewModel,
                painter = painter,
                onMenuClick = { isMenuOpen.value = true },
                navController = navController,
                isFullScreen = mainViewModel.isFullScreen.value,
                onFullScreenChange = { mainViewModel.setFullScreen(it) }
            )
        }
        composable(Screen.Upload.route) {
            UploadScreen(
                navController = navController,
                painter = painter,
                onMenuClick = { isMenuOpen.value = true }
            )
        }
        composable(Screen.Library.route) {
            LibraryScreen(
                navController = navController,
                painter = painter,
                onMenuClick = { isMenuOpen.value = true }
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



    }

    ProfileMenu(isMenuOpen = isMenuOpen, navController = navController)
}

