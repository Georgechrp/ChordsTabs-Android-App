package com.unipi.george.chordshub.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.unipi.george.chordshub.components.MyAppTopBar
import com.unipi.george.chordshub.viewmodels.main.LibraryViewModel
import com.unipi.george.chordshub.viewmodels.MainViewModel

@Composable
fun LibraryScreen(navController: NavController, painter: Painter, mainViewModel: MainViewModel, onMenuClick: () -> Unit) {
    val viewModel: LibraryViewModel = viewModel()
    val playlists by viewModel.playlists.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var playlistName by remember { mutableStateOf("") }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            MyAppTopBar(
                painter = painter,
                onMenuClick = onMenuClick
            ) {
                Text("Βιβλιοθήκη", style = MaterialTheme.typography.headlineSmall)
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Text("+", style = MaterialTheme.typography.headlineSmall)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (playlists.isEmpty()) {
                Text("Δεν υπάρχουν playlists ακόμα.")
            } else {
                playlists.forEach { playlist ->
                    Text(playlist, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }

    // Διάλογος για δημιουργία playlist
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Δημιουργία Playlist") },
            text = {
                Column {
                    Text("Δώσε ένα όνομα για τη νέα playlist σου:")
                    OutlinedTextField(
                        value = playlistName,
                        onValueChange = { playlistName = it },
                        label = { Text("Όνομα Playlist") }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (playlistName.isNotBlank()) {
                        viewModel.createPlaylist(playlistName) { success ->
                            if (success) {
                                showDialog = false
                                playlistName = ""
                            }
                        }
                    }
                }) {
                    Text("Δημιουργία")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Άκυρο")
                }
            }
        )
    }
}
