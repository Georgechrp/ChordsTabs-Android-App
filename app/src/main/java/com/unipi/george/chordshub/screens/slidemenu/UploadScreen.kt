package com.unipi.george.chordshub.screens.slidemenu

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.unipi.george.chordshub.R
import com.unipi.george.chordshub.models.song.ChordPosition
import com.unipi.george.chordshub.models.song.Song
import com.unipi.george.chordshub.models.song.SongLine
import com.unipi.george.chordshub.repository.AuthRepository
import com.unipi.george.chordshub.repository.FirestoreRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadScreen(navController: NavController, painter: Painter, onMenuClick: () -> Unit) {
    val repository = FirestoreRepository(FirebaseFirestore.getInstance())
    val coroutineScope = rememberCoroutineScope()

    var title by remember { mutableStateOf("") }
    var artist by remember { mutableStateOf("") }
    var key by remember { mutableStateOf("") }
    var bpm by remember { mutableStateOf("") }
    var lyrics by remember { mutableStateOf("") }
    var chords by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {  Text(stringResource(R.string.add_song_text)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Πίσω")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Τίτλος") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = artist,
                onValueChange = { artist = it },
                label = { Text("Καλλιτέχνης") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = key,
                onValueChange = { key = it },
                label = { Text("Τονικό Ύψος (Key)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = bpm,
                onValueChange = { bpm = it },
                label = { Text("BPM") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = lyrics,
                onValueChange = { lyrics = it },
                label = { Text("Στίχοι (γραμμές χωρισμένες με enter)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = chords,
                onValueChange = { chords = it },
                label = { Text("Συγχορδίες (μορφή: Bb-0, Gm-5)") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    coroutineScope.launch {
                        val currentUserId = AuthRepository.getUserId()
                        val songId = title.replace(" ", "_").lowercase()

                        val songLines = lyrics.split("\n").mapIndexed { index, line ->
                            SongLine(
                                lineNumber = index + 1,
                                text = line,
                                chords = chords.split(",").mapNotNull { chordData ->
                                    val parts = chordData.split("-")
                                    if (parts.size == 2) {
                                        val chord = parts[0]
                                        val position = parts[1].toIntOrNull()
                                        if (position != null) ChordPosition(chord, position) else null
                                    } else null
                                }
                            )
                        }

                        val song = Song(
                            title = title,
                            artist = artist,
                            key = key,
                            bpm = bpm.toIntOrNull() ?: 0,
                            genres = listOf("User Added"),
                            createdAt = System.currentTimeMillis().toString(),
                            creatorId = currentUserId,
                            lyrics = songLines
                        )

                        repository.addSongData(songId, song)
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Στείλε το τραγούδι")
            }
        }
    }
}
