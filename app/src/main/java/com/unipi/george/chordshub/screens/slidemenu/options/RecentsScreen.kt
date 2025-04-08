package com.unipi.george.chordshub.screens.slidemenu.options

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.unipi.george.chordshub.R
import com.unipi.george.chordshub.components.LoadingView
import com.unipi.george.chordshub.repository.AuthRepository
import com.unipi.george.chordshub.screens.viewsong.DetailedSongView
import com.unipi.george.chordshub.viewmodels.main.HomeViewModel
import com.unipi.george.chordshub.viewmodels.user.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentsScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    homeViewModel: HomeViewModel
) {
    val recentSongs by userViewModel.recentSongs
    val userId = AuthRepository.getUserId()
    var isLoading by remember { mutableStateOf(true) }
    val selectedSongId by homeViewModel.selectedSongId.collectAsState()

    // Fetch recent songs κατά την είσοδο στην οθόνη
    LaunchedEffect(userId) {
        if (userId != null) {
            userViewModel.fetchRecentSongs(userId)
        }
        isLoading = false
    }

    // Αν έχει επιλεγεί τραγούδι, δείχνουμε την λεπτομέρεια του
    if (selectedSongId != null) {
        DetailedSongView(
            songId = selectedSongId!!,
            isFullScreenState = false,
            onBack = {
                homeViewModel.clearSelectedSong()
            },
            navController = navController,
            mainViewModel = null, // Βάλε αν το χρειάζεσαι
            homeViewModel = homeViewModel,
            userViewModel = userViewModel
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.recent_text)) },
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
            when {
                isLoading -> {
                    LoadingView()
                }

                recentSongs.isEmpty() -> {
                    Text("Δεν υπάρχουν πρόσφατα τραγούδια.", style = MaterialTheme.typography.bodyLarge)
                }

                else -> {
                    recentSongs.forEach { song ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable {
                                    homeViewModel.selectSong(song)
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
}
