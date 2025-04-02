package com.unipi.george.chordshub.screens.auth.welcomeuser

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import com.unipi.george.chordshub.viewmodels.user.SessionViewModel
import com.unipi.george.chordshub.navigation.AppScreens

@Composable
fun WelcomeScreen(
    navController: NavController,
    sessionViewModel: SessionViewModel
) {
    // Μόλις ξεκινήσει το composable, κάνε delay και πήγαινε στη σωστή οθόνη
    LaunchedEffect(Unit) {
        delay(2000L) // 2 δευτερόλεπτα splash
        if (sessionViewModel.isUserLoggedInState.value) {
            navController.navigate(AppScreens.Main.route) {
                popUpTo(AppScreens.Welcome.route) { inclusive = true }
            }
        } else {
            navController.navigate(AppScreens.Auth.route) {
                popUpTo(AppScreens.Welcome.route) { inclusive = true }
            }
        }
    }

    // UI εμφάνισης του welcome
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🎸 Chords & Tabs", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }
    }
}
