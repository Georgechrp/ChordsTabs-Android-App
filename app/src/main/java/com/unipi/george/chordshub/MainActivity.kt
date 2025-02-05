package com.unipi.george.chordshub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.unipi.george.chordshub.components.TopBar
import com.unipi.george.chordshub.navigation.BottomNavigationBar
import com.unipi.george.chordshub.navigation.Screen
import com.unipi.george.chordshub.repository.AuthRepository
import com.unipi.george.chordshub.screens.HomeScreen
import com.unipi.george.chordshub.screens.LibraryScreen
import com.unipi.george.chordshub.screens.LoginScreen
import com.unipi.george.chordshub.screens.ProfileSettings
import com.unipi.george.chordshub.screens.SearchScreen
import com.unipi.george.chordshub.screens.SignUpScreen
import com.unipi.george.chordshub.ui.theme.ChordsHubTheme
import com.unipi.george.chordshub.viewmodels.HomeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChordsHubTheme {
                val navController = rememberNavController()
                val isUserLoggedInState = AuthRepository.isUserLoggedInState
                val fullNameState = AuthRepository.fullNameState
                val homeViewModel: HomeViewModel = viewModel()

                if (isUserLoggedInState.value) {
                    LoggedInScaffold(navController, fullNameState, isUserLoggedInState, homeViewModel)
                } else {
                    LoggedOutNavHost(navController, isUserLoggedInState, fullNameState)
                }
            }
        }
    }
}

@Composable
fun LoggedInScaffold(
    navController: NavHostController,
    fullNameState: MutableState<String?>,
    isUserLoggedInState: MutableState<Boolean>,
    homeViewModel: HomeViewModel
) {
    val isMenuOpen = remember { mutableStateOf(false) }
    val isFullScreen = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopBar(
                fullName = fullNameState.value ?: "User",
                painter = painterResource(id = R.drawable.user_icon),
                navController = navController,
                isVisible = !isFullScreen.value,
                onMenuClick = { isMenuOpen.value = !isMenuOpen.value },
                selectedSong = homeViewModel.selectedSong.collectAsState().value // Περνάμε το τραγούδι στην TopBar
            )

            Scaffold(
                bottomBar = {
                    if (!isFullScreen.value) {
                        BottomNavigationBar(navController)
                    }
                }
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route,
                    modifier = Modifier.padding(innerPadding)
                ) {
                    composable(Screen.Home.route) {
                        HomeScreen(
                            navController = navController,
                            isFullScreen = isFullScreen.value,
                            onFullScreenChange = { isFullScreen.value = it },
                            homeViewModel = homeViewModel // Περνάμε το ViewModel
                        )
                    }
                    composable(Screen.Search.route) { SearchScreen(navController) }
                    composable(Screen.Library.route) { LibraryScreen(navController) }
                }
            }
        }
    }
    AnimatedVisibility(visible = isMenuOpen.value) {
        ProfileSettings(
            isMenuOpen = isMenuOpen, // ✅ Περνάμε το state
            navController = navController,
            onLogout = {
                isUserLoggedInState.value = false
                fullNameState.value = null
                AuthRepository.logoutUser()
            }
        )
    }
}

@Composable
fun LoggedOutNavHost(
    navController: NavHostController,
    isUserLoggedInState: MutableState<Boolean>,
    fullNameState: MutableState<String?>
) {
    NavHost(
        navController = navController,
        startDestination = "Login"
    ) {
        composable("Login") {
            LoginScreen(navController) {
                isUserLoggedInState.value = true
                fullNameState.value = AuthRepository.getFullName()
            }
        }
        composable("SignUp") {
            SignUpScreen(navController)
        }
    }
}


