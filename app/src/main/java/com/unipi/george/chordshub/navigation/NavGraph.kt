package com.unipi.george.chordshub.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.unipi.george.chordshub.R
import com.unipi.george.chordshub.repository.AuthRepository
import com.unipi.george.chordshub.repository.AuthRepository.fullNameState
import com.unipi.george.chordshub.repository.AuthRepository.isUserLoggedInState
import com.unipi.george.chordshub.screens.main.HomeScreen
import com.unipi.george.chordshub.screens.main.LibraryScreen
import com.unipi.george.chordshub.screens.main.SearchScreen
import com.unipi.george.chordshub.screens.main.UploadScreen
import com.unipi.george.chordshub.screens.main.ProfileScreen
import com.unipi.george.chordshub.viewmodels.HomeViewModel
import com.unipi.george.chordshub.viewmodels.SearchViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    isFullScreen: MutableState<Boolean>
) {
    val homeViewModel: HomeViewModel = viewModel()
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            var selectedFilter by remember { mutableStateOf("All") }
            LaunchedEffect(Unit) { selectedFilter = "All" }

            HomeScreen(
                isFullScreen = isFullScreen.value,
                onFullScreenChange = { isFullScreen.value = it },
                homeViewModel = homeViewModel,
                selectedFilter = selectedFilter,
                navController
            )
        }

        composable(Screen.Search.route) {
            val searchViewModel: SearchViewModel = viewModel()

            SearchScreen(
                viewModel = searchViewModel,
                painter = painterResource(id = R.drawable.user_icon), // ✅ Περνάμε το εικονίδιο
                onMenuClick = { /* Εδώ βάζεις το άνοιγμα του μενού */ }
            )
        }


        composable(Screen.Library.route) {
            LibraryScreen()
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

        composable(Screen.Upload.route) {
            UploadScreen(navController)
        }
    }
}
