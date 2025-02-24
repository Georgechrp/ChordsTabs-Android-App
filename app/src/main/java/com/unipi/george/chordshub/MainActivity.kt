package com.unipi.george.chordshub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.unipi.george.chordshub.models.ChordPosition
import com.unipi.george.chordshub.models.SongData
import com.unipi.george.chordshub.navigation.BottomNavigationBar
import com.unipi.george.chordshub.repository.AuthRepository
import com.unipi.george.chordshub.repository.FirestoreRepository
import com.unipi.george.chordshub.screens.auth.LoginScreen
import com.unipi.george.chordshub.screens.auth.SignUpScreen
import com.unipi.george.chordshub.ui.theme.ChordsHubTheme
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import com.unipi.george.chordshub.models.SongLine
import com.unipi.george.chordshub.navigation.NavGraph


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //addDataForFurElise()
        setContent {
            ChordsHubTheme {
                val navController = rememberNavController()
                val isUserLoggedInState = AuthRepository.isUserLoggedInState
                val fullNameState = AuthRepository.fullNameState

                if (isUserLoggedInState.value) {
                    LoggedInScaffold(navController)
                } else {
                    LoggedOutNavHost(navController, isUserLoggedInState, fullNameState)
                }
            }
        }
    }
    private fun addDataForFurElise() {
        val repository = FirestoreRepository(FirebaseFirestore.getInstance())

        lifecycleScope.launch {
            val songData = SongData(
                title = "Für Elise",
                artist = "Ludwig van Beethoven",
                key = "A Minor",
                bpm = 75, // Μέτριο tempo
                genres = listOf("Classical"),
                createdAt = System.currentTimeMillis().toString(),
                creatorId = "admin",
                lyrics = listOf(
                    SongLine(
                        lineNumber = 1,
                        text = "Für Elise, section 1 melody",
                        chords = listOf(ChordPosition("Am", 0), ChordPosition("E", 8), ChordPosition("G", 16), ChordPosition("C", 24))
                    ),
                    SongLine(
                        lineNumber = 2,
                        text = "Für Elise, section 2 transition",
                        chords = listOf(ChordPosition("F", 0), ChordPosition("C", 10), ChordPosition("G", 20), ChordPosition("E", 28))
                    ),
                    SongLine(
                        lineNumber = 3,
                        text = "Für Elise, section 3 dramatic part",
                        chords = listOf(ChordPosition("Am", 0), ChordPosition("D", 12), ChordPosition("G", 20), ChordPosition("E", 30))
                    ),
                    SongLine(
                        lineNumber = 3,
                        text = "Für Elise, section 3 dramatic part",
                        chords = listOf(ChordPosition("Am", 0), ChordPosition("D", 12), ChordPosition("G", 20), ChordPosition("E", 30))
                    ),
                    SongLine(
                        lineNumber = 3,
                        text = "Für Elise, section 3 dramatic part",
                        chords = listOf(ChordPosition("Am", 0), ChordPosition("D", 12), ChordPosition("G", 20), ChordPosition("E", 30))
                    ),
                    SongLine(
                        lineNumber = 3,
                        text = "Für Elise, section 3 dramatic part",
                        chords = listOf(ChordPosition("Am", 0), ChordPosition("D", 12), ChordPosition("G", 20), ChordPosition("E", 30))
                    ),
                    SongLine(
                        lineNumber = 3,
                        text = "Für Elise, section 3 dramatic part",
                        chords = listOf(ChordPosition("Am", 0), ChordPosition("D", 12), ChordPosition("G", 20), ChordPosition("E", 30))
                    ),
                    SongLine(
                        lineNumber = 3,
                        text = "Für Elise, section 3 dramatic part",
                        chords = listOf(ChordPosition("Am", 0), ChordPosition("D", 12), ChordPosition("G", 20), ChordPosition("E", 30))
                    ),
                    SongLine(
                        lineNumber = 3,
                        text = "Für Elise, section 3 dramatic part",
                        chords = listOf(ChordPosition("Am", 0), ChordPosition("D", 12), ChordPosition("G", 20), ChordPosition("E", 30))
                    ),
                    SongLine(
                        lineNumber = 3,
                        text = "Für Elise, section 3 dramatic part",
                        chords = listOf(ChordPosition("Am", 0), ChordPosition("D", 12), ChordPosition("G", 20), ChordPosition("E", 30))
                    ),
                    SongLine(
                        lineNumber = 3,
                        text = "Für Elise, section 3 dramatic part",
                        chords = listOf(ChordPosition("Am", 0), ChordPosition("D", 12), ChordPosition("G", 20), ChordPosition("E", 30))
                    ),
                    SongLine(
                        lineNumber = 3,
                        text = "Für Elise, section 3 dramatic part",
                        chords = listOf(ChordPosition("Am", 0), ChordPosition("D", 12), ChordPosition("G", 20), ChordPosition("E", 30))
                    ),
                    SongLine(
                        lineNumber = 3,
                        text = "Für Elise, section 3 dramatic part",
                        chords = listOf(ChordPosition("Am", 0), ChordPosition("D", 12), ChordPosition("G", 20), ChordPosition("E", 30))
                    ),
                    SongLine(
                        lineNumber = 3,
                        text = "Für Elise, section 3 dramatic part",
                        chords = listOf(ChordPosition("Am", 0), ChordPosition("D", 12), ChordPosition("G", 20), ChordPosition("E", 30))
                    ),
                    SongLine(
                        lineNumber = 3,
                        text = "Für Elise, section 3 dramatic part",
                        chords = listOf(ChordPosition("Am", 0), ChordPosition("D", 12), ChordPosition("G", 20), ChordPosition("E", 30))
                    ),
                    SongLine(
                        lineNumber = 3,
                        text = "Für Elise, section 3 dramatic part",
                        chords = listOf(ChordPosition("Am", 0), ChordPosition("D", 12), ChordPosition("G", 20), ChordPosition("E", 30))
                    ),
                    SongLine(
                        lineNumber = 3,
                        text = "Für Elise, section 3 dramatic part",
                        chords = listOf(ChordPosition("Am", 0), ChordPosition("D", 12), ChordPosition("G", 20), ChordPosition("E", 30))
                    ),
                    SongLine(
                        lineNumber = 3,
                        text = "Für Elise, section 3 dramatic part",
                        chords = listOf(ChordPosition("Am", 0), ChordPosition("D", 12), ChordPosition("G", 20), ChordPosition("E", 30))
                    ),
                    SongLine(
                        lineNumber = 3,
                        text = "Für Elise, section 3 dramatic part",
                        chords = listOf(ChordPosition("Am", 0), ChordPosition("D", 12), ChordPosition("G", 20), ChordPosition("E", 30))
                    ),
                    SongLine(
                        lineNumber = 3,
                        text = "Für Elise, section 3 dramatic part",
                        chords = listOf(ChordPosition("Am", 0), ChordPosition("D", 12), ChordPosition("G", 20), ChordPosition("E", 30))
                    ),
                    SongLine(
                        lineNumber = 3,
                        text = "Für Elise, section 3 dramatic part",
                        chords = listOf(ChordPosition("Am", 0), ChordPosition("D", 12), ChordPosition("G", 20), ChordPosition("E", 30))
                    ),
                    SongLine(
                        lineNumber = 3,
                        text = "Für Elise, section 3 dramatic part",
                        chords = listOf(ChordPosition("Am", 0), ChordPosition("D", 12), ChordPosition("G", 20), ChordPosition("E", 30))
                    ),
                    SongLine(
                        lineNumber = 3,
                        text = "Für Elise, section 3 dramatic part",
                        chords = listOf(ChordPosition("Am", 0), ChordPosition("D", 12), ChordPosition("G", 20), ChordPosition("E", 30))
                    ),
                    SongLine(
                        lineNumber = 3,
                        text = "Für Elise, section 3 dramatic part",
                        chords = listOf(ChordPosition("Am", 0), ChordPosition("D", 12), ChordPosition("G", 20), ChordPosition("E", 30))
                    ),
                    SongLine(
                        lineNumber = 3,
                        text = "Für Elise, section 3 dramatic part",
                        chords = listOf(ChordPosition("Am", 0), ChordPosition("D", 12), ChordPosition("G", 20), ChordPosition("E", 30))
                    ),
                    SongLine(
                        lineNumber = 3,
                        text = "Für Elise, section 3 dramatic part",
                        chords = listOf(ChordPosition("Am", 0), ChordPosition("D", 12), ChordPosition("G", 20), ChordPosition("E", 30))
                    ),
                    SongLine(
                        lineNumber = 3,
                        text = "Für Elise, section 3 dramatic part",
                        chords = listOf(ChordPosition("Am", 0), ChordPosition("D", 12), ChordPosition("G", 20), ChordPosition("E", 30))
                    ),
                    SongLine(
                        lineNumber = 3,
                        text = "Für Elise, section 3 dramatic part",
                        chords = listOf(ChordPosition("Am", 0), ChordPosition("D", 12), ChordPosition("G", 20), ChordPosition("E", 30))
                    ),
                    SongLine(
                        lineNumber = 3,
                        text = "Für Elise, section 3 dramatic part",
                        chords = listOf(ChordPosition("Am", 0), ChordPosition("D", 12), ChordPosition("G", 20), ChordPosition("E", 30))
                    ),
                    SongLine(
                        lineNumber = 3,
                        text = "Für Elise, section 3 dramatic part",
                        chords = listOf(ChordPosition("Am", 0), ChordPosition("D", 12), ChordPosition("G", 20), ChordPosition("E", 30))
                    ),
                    SongLine(
                        lineNumber = 3,
                        text = "Für Elise, section 3 dramatic part",
                        chords = listOf(ChordPosition("Am", 0), ChordPosition("D", 12), ChordPosition("G", 20), ChordPosition("E", 30))
                    )
                )
            )

            repository.addSongData("fur_elise_beethoven", songData)
        }
    }
}

@Composable
fun LoggedInScaffold(
    navController: NavHostController
) {
    val isMenuOpen = remember { mutableStateOf(false) }
    val isFullScreen = remember { mutableStateOf(false) }

    BackHandler(enabled = isMenuOpen.value) {
        isMenuOpen.value = false
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                bottomBar = {
                    if (!isFullScreen.value) {
                        BottomNavigationBar(navController)
                    }
                }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    NavGraph(navController, isFullScreen)
                }
            }
        }

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


