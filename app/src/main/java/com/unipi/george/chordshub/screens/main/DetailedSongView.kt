package com.unipi.george.chordshub.screens.main

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import com.unipi.george.chordshub.components.ChordText
import com.unipi.george.chordshub.models.SongLine
import com.unipi.george.chordshub.models.SongData
import com.unipi.george.chordshub.repository.FirestoreRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DetailedSongView(
    isFullScreen: Boolean,
    onFullScreenChange: (Boolean) -> Unit,
    onBack: () -> Unit,
    repository: FirestoreRepository = FirestoreRepository(FirebaseFirestore.getInstance()) // Δίνουμε τη δυνατότητα στο repository να περαστεί εξωτερικά για testing
) {
    val songDataState = remember { mutableStateOf<SongData?>(null) }
    val isScrolling = remember { mutableStateOf(false) }
    val scrollSpeed = remember { mutableStateOf(100f) }
    val isSpeedControlVisible = remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val showDialog = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        songDataState.value = repository.getSongDataAsync()
    }

    LaunchedEffect(isScrolling.value) {
        while (isScrolling.value) {
            coroutineScope.launch {
                listState.animateScrollBy(10f)
            }
            delay(scrollSpeed.value.toLong())
        }
    }

    BackHandler {
        if (isFullScreen) {
            onFullScreenChange(false)
        } else {
            onBack()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onFullScreenChange(!isFullScreen) }
    ) {
        if (songDataState.value == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Loading...")
            }
        } else {
            val songData = songDataState.value!!

            Card(
                modifier = if (isFullScreen) Modifier.fillMaxSize() else Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(if (isFullScreen) 0.dp else 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isFullScreen) 0.dp else 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Εμφάνιση πληροφοριών τραγουδιού (τίτλος και artist)
                        SongInfoPlace(
                            title = songData.title ?: "No Title",
                            artist = songData.artist ?: "Unknown Artist",
                            isFullScreen = isFullScreen,
                            modifier = Modifier.weight(1f)
                        )
                        // Εμφάνιση επιλογών (auto-scroll, διαχείριση ταχύτητας, κλπ.)
                        OptionsPlace(isScrolling, isSpeedControlVisible, showDialog)
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    ControlSpeed(scrollSpeed, isSpeedControlVisible)

                    Spacer(modifier = Modifier.height(8.dp))

                    // ✅ Νέα συνάρτηση που εμφανίζει τα lyrics με συγχορδίες
                    SongLyricsView(songLines = songData.lyrics ?: emptyList(), listState = listState)

                    OptionsDialog(showDialog)
                }
            }
        }
    }
}

@Composable
fun SongInfoPlace(title: String, artist: String, isFullScreen: Boolean, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = if (isFullScreen) 18.sp else 16.sp
        )
        Text(
            text = artist,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = if (isFullScreen) 16.sp else 14.sp,
                textDecoration = TextDecoration.Underline,
                color = Color.Blue
            ),
            modifier = Modifier.clickable {
                Log.d("Artist Click", "Clicked on artist: $artist")
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OptionsPlace(
    isScrolling: MutableState<Boolean>,
    isSpeedControlVisible: MutableState<Boolean>,
    showDialog: MutableState<Boolean>
) {
    Row {
        Box(
            modifier = Modifier
                .combinedClickable(
                    onClick = { isScrolling.value = !isScrolling.value },
                    onLongClick = { isSpeedControlVisible.value = true }
                )
                .padding(8.dp)
        ) {
            Icon(
                imageVector = if (isScrolling.value) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isScrolling.value) "Pause Auto-Scroll" else "Start Auto-Scroll"
            )
        }
        IconButton(onClick = { showDialog.value = true }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More Options"
            )
        }
    }
}

@Composable
fun ControlSpeed(scrollSpeed: MutableState<Float>, isSpeedControlVisible: MutableState<Boolean>) {
    if (isSpeedControlVisible.value) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Scroll Speed", fontSize = 14.sp)
                IconButton(onClick = { isSpeedControlVisible.value = false }) {
                    Text("❌")
                }
            }
            Slider(
                value = scrollSpeed.value,
                onValueChange = { scrollSpeed.value = it },
                valueRange = 10f..200f,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun SongLyricsView(songLines: List<SongLine>, listState: LazyListState) {
    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp)
    ) {
        items(songLines) { line ->
            Column(
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                ChordText(songLine = line, onChordClick = { clickedChord ->
                    Log.d("Chord Click", "Clicked on: $clickedChord")
                })
            }
        }
    }
}



@Composable
fun OptionsDialog(showDialog: MutableState<Boolean>) {
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            confirmButton = {
                Text(
                    text = "OK",
                    modifier = Modifier.clickable { showDialog.value = false }
                )
            },
            dismissButton = {
                Text(
                    text = "Save as PDF",
                    modifier = Modifier.clickable {
                        // Εδώ μπορείς να προσθέσεις τη λογική αποθήκευσης ως PDF
                        showDialog.value = false
                    }
                )
            },
            title = { Text("Επιλογές") },
            text = { Text("Εδώ μπορείς να προσθέσεις επιλογές για το τραγούδι.") }
        )
    }
}
