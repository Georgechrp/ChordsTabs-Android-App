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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.unipi.george.chordshub.components.ChordPosition
import com.unipi.george.chordshub.components.TopBar
import com.unipi.george.chordshub.models.SongData
import com.unipi.george.chordshub.navigation.BottomNavigationBar
import com.unipi.george.chordshub.navigation.Screen
import com.unipi.george.chordshub.repository.AuthRepository
import com.unipi.george.chordshub.repository.FirestoreRepository
import com.unipi.george.chordshub.screens.main.HomeScreen
import com.unipi.george.chordshub.screens.main.LibraryScreen
import com.unipi.george.chordshub.screens.auth.LoginScreen
import com.unipi.george.chordshub.screens.profile.ProfileSettings
import com.unipi.george.chordshub.screens.main.SearchScreen
import com.unipi.george.chordshub.screens.auth.SignUpScreen
import com.unipi.george.chordshub.ui.theme.ChordsHubTheme
import com.unipi.george.chordshub.viewmodels.HomeViewModel
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //addDataForBohemianRhapsody()
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

    private fun addDataForBohemianRhapsody() {
        val repository = FirestoreRepository(FirebaseFirestore.getInstance())

        // Χρησιμοποιούμε το lifecycleScope για να συνδέσουμε το coroutine με τον κύκλο ζωής της activity
        lifecycleScope.launch {
            val songData = SongData(
                title = "Bohemian Rhapsody",
                artist = "Queen",
                key = "Bb", // Μπορείς να προσαρμόσεις το key ανάλογα με την εκδοχή
                lyrics = listOf(
                    "Is this the real life?",
                    "Is this just fantasy?",
                    "Caught in a landslide, no escape from reality.",
                    "Open your eyes, look up to the skies and see..."
                ),
                chords = listOf(
                    ChordPosition("Bb", 0),
                    ChordPosition("Gm", 5),
                    ChordPosition("Eb", 10),
                    ChordPosition("F", 15)
                ),
                genres = listOf("Rock")
            )

            repository.addSongData("bohemian_rhapsody_queen", songData)
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
    var selectedFilter by remember { mutableStateOf("All") }


    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopBar(
                fullName = fullNameState.value ?: "User",
                painter = painterResource(id = R.drawable.user_icon),
                navController = navController,
                isVisible = !isFullScreen.value,
                onMenuClick = { isMenuOpen.value = !isMenuOpen.value },
                selectedSong = homeViewModel.selectedSong.collectAsState().value,
                onFilterChange = { selectedFilter = it }
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
                            homeViewModel = homeViewModel,
                            selectedFilter = selectedFilter
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
            isMenuOpen = isMenuOpen,
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


