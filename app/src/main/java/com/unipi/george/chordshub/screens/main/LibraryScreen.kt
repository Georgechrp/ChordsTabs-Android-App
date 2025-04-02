package com.unipi.george.chordshub.screens.main

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.unipi.george.chordshub.R
import com.unipi.george.chordshub.components.MyAppTopBar
import com.unipi.george.chordshub.viewmodels.main.LibraryViewModel
import com.unipi.george.chordshub.viewmodels.MainViewModel

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(navController: NavController, painter: Painter, mainViewModel: MainViewModel, onMenuClick: () -> Unit) {
    val viewModel: LibraryViewModel = viewModel()
    val playlists by viewModel.playlists.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var playlistName by remember { mutableStateOf("") }
    var showAddSongDialog by remember { mutableStateOf(false) }
    var selectedPlaylist by remember { mutableStateOf<String?>(null) }
    var songTitle by remember { mutableStateOf("") }
    val duplicateError = remember { mutableStateOf(false) }
    val showBottomSheet = remember { mutableStateOf(false) }

    var showRenameDialog by remember { mutableStateOf(false) }
    var newPlaylistName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            MyAppTopBar(
                mainViewModel = mainViewModel,
                onMenuClick = onMenuClick
            ) {
                Text("Βιβλιοθήκη", style = MaterialTheme.typography.headlineSmall)
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                val existingNames = playlists.keys

                // Βρίσκουμε το μικρότερο διαθέσιμο όνομα
                var nextNumber = 1
                while (existingNames.contains("My Playlist #$nextNumber")) {
                    nextNumber++
                }

                playlistName = "My Playlist #$nextNumber"
                showDialog = true
            },
                modifier = Modifier.padding(bottom = 76.dp)
            )
            {
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
                playlists.forEach { (playlist, songs) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .combinedClickable(
                                onClick = {
                                    navController.navigate("playlist_detail/${Uri.encode(playlist)}")
                                },
                                onLongClick = {
                                    selectedPlaylist = playlist
                                    showBottomSheet.value = true
                                }
                            )
                    )
                    {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(playlist, style = MaterialTheme.typography.bodyLarge)

                            songs.forEach { song ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(song, style = MaterialTheme.typography.bodyMedium)
                                    TextButton(onClick = {
                                        viewModel.removeSongFromPlaylist(playlist, song) {}
                                    }) {
                                        Text("❌")
                                    }
                                }
                            }

                            /*Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                TextButton(onClick = {
                                    selectedPlaylist = playlist
                                    showAddSongDialog = true
                                }) {
                                    Text("➕ Προσθήκη τραγουδιού")
                                }

                                TextButton(onClick = {
                                    viewModel.deletePlaylist(playlist) {}
                                }) {
                                    Text("🗑️")
                                }
                            }*/
                        }
                    }
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
                        onValueChange = {
                            playlistName = it
                            duplicateError.value = false // reset error όταν ο χρήστης αλλάζει κάτι
                        },
                        label = { Text("Όνομα Playlist") }
                    )
                    if (duplicateError.value) {
                        Text(
                            text = stringResource(R.string.already_exists_the_name_of_playlist),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (playlistName.isNotBlank()) {
                        if (playlistName in playlists.keys) {
                            duplicateError.value = true
                        } else {
                            viewModel.createPlaylist(playlistName) { success ->
                                if (success) {
                                    showDialog = false
                                    playlistName = ""
                                    duplicateError.value = false
                                }
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

    // Διάλογος για προσθήκη τραγουδιού σε playlist
    if (showAddSongDialog) {
        AlertDialog(
            onDismissRequest = { showAddSongDialog = false },
            title = { Text("Προσθήκη Τραγουδιού") },
            text = {
                Column {
                    Text("Δώσε το όνομα του τραγουδιού:")
                    OutlinedTextField(
                        value = songTitle,
                        onValueChange = { songTitle = it },
                        label = { Text("Όνομα Τραγουδιού") }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (songTitle.isNotBlank() && selectedPlaylist != null) {
                        viewModel.addSongToPlaylist(selectedPlaylist!!, songTitle) { success ->
                            if (success) {
                                showAddSongDialog = false
                                songTitle = ""
                            }
                        }
                    }
                }) {
                    Text("Προσθήκη")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddSongDialog = false }) {
                    Text("Άκυρο")
                }
            }
        )
    }

    //Playlists's options
    if (showBottomSheet.value && selectedPlaylist != null) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet.value = false
                selectedPlaylist = null
            },
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = selectedPlaylist ?: "",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                TextButton(onClick = {
                    newPlaylistName = selectedPlaylist ?: ""
                    showRenameDialog = true
                    showBottomSheet.value = false
                }) {
                    Text(stringResource(R.string.rename_playlist_text))
                }



                TextButton(onClick = {
                    showAddSongDialog = true
                    showBottomSheet.value = false
                }) {
                    Text(stringResource(R.string.add_song_text2))
                }

                TextButton(onClick = {
                    viewModel.deletePlaylist(selectedPlaylist!!) {}
                    showBottomSheet.value = false
                }) {
                    Text(stringResource(R.string.delete_playlist_text))
                }
            }
        }
    }

    //Rename dialog
    if (showRenameDialog && selectedPlaylist != null) {
        AlertDialog(
            onDismissRequest = {
                showRenameDialog = false
                newPlaylistName = ""
            },
            title = { Text(stringResource(R.string.rename_playlist_text)) },
            text = {
                Column {
                    Text(stringResource(R.string.give_a_name_to_playlist))
                    OutlinedTextField(
                        value = newPlaylistName,
                        onValueChange = { newPlaylistName = it },
                        label = { Text(stringResource(R.string.new_name_text)) }
                    )
                    if (newPlaylistName in playlists.keys && newPlaylistName != selectedPlaylist) {
                        Text(
                            text = stringResource(R.string.already_exists_the_name_of_playlist),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newPlaylistName.isNotBlank()
                        && newPlaylistName != selectedPlaylist
                        && newPlaylistName !in playlists.keys
                    ) {
                        viewModel.renamePlaylist(selectedPlaylist!!, newPlaylistName) {
                            showRenameDialog = false
                            selectedPlaylist = null
                            newPlaylistName = ""
                        }
                    }
                }) {
                    Text(stringResource(R.string.save_button_text))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showRenameDialog = false
                    newPlaylistName = ""
                }) {
                    Text(stringResource(R.string.cancel_button_text))
                }
            }
        )
    }

}


