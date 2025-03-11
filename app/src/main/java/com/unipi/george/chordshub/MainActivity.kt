package com.unipi.george.chordshub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.unipi.george.chordshub.repository.AuthRepository
import com.unipi.george.chordshub.ui.theme.ChordsHubTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unipi.george.chordshub.navigation.auth.AuthFlowNavGraph
import com.unipi.george.chordshub.navigation.main.MainScaffold
import com.unipi.george.chordshub.repository.UserStatsRepository
import com.unipi.george.chordshub.utils.UserSessionManager
import com.unipi.george.chordshub.viewmodels.user.UserViewModel

class MainActivity : ComponentActivity() {
    private val sessionManager by lazy {
        UserSessionManager(UserStatsRepository(FirebaseFirestore.getInstance()))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChordsHubTheme {
                val userViewModel: UserViewModel = viewModel()
                ObserveUserSession(userViewModel)
                val navController = rememberNavController()
                val isUserLoggedInState = AuthRepository.isUserLoggedInState

                if (isUserLoggedInState.value) {
                    MainScaffold(navController)
                } else {
                    AuthFlowNavGraph(navController, isUserLoggedInState)
                }
            }
        }
    }

    @Composable
    private fun ObserveUserSession(userViewModel: UserViewModel) {
        val userId = userViewModel.userId
        LaunchedEffect(userId) {
            if (!userId.isNullOrEmpty()) {
                sessionManager.startSession(userId)
            } else {
                sessionManager.endSession(false)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        sessionManager.endSession(isChangingConfigurations)
    }
}


/*
fun addDataForFurElise() {
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
                )
            )
        )

        repository.addSongData("fur_elise_beethoven", songData)
    }
}*/
