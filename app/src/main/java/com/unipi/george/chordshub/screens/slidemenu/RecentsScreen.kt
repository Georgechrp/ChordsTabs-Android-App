package com.unipi.george.chordshub.screens.slidemenu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.unipi.george.chordshub.repository.AuthRepository
import com.unipi.george.chordshub.viewmodels.user.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentsScreen(
    navController: NavController,
    userViewModel: UserViewModel
) {
    val recentSongs by userViewModel.recentSongs
    val userId = AuthRepository.getUserId()

    // Fetch recent songs κατά την είσοδο στην οθόνη
    LaunchedEffect(Unit) {
        userId?.let { userViewModel.fetchRecentSongs(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Πρόσφατα") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Πίσω")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (recentSongs.isEmpty()) {
                Text("Δεν υπάρχουν πρόσφατα τραγούδια.", style = MaterialTheme.typography.bodyLarge)
            } else {
                recentSongs.forEach { song ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                // Πλοήγηση στη DetailedSongView με το όνομα του τραγουδιού
                                navController.navigate("detailedSongView/${song}")
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text(
                            text = song,
                            modifier = Modifier.padding(16.dp),
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}
