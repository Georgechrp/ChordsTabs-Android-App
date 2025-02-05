package com.unipi.george.chordshub.screens

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import com.unipi.george.chordshub.components.ChordText
import com.unipi.george.chordshub.components.SongLine
import com.unipi.george.chordshub.repository.FirestoreRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DetailedSongView(title: String, artist:String, song: SongLine, isFullScreen: Boolean, onFullScreenChange: (Boolean) -> Unit, onBack: () -> Unit) {
    val showDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val isScrolling = remember { mutableStateOf(false) }
    val scrollSpeed = remember { mutableStateOf(100f) }
    val isSpeedControlVisible = remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val repository = remember { FirestoreRepository(FirebaseFirestore.getInstance()) }
    val songTitle = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        repository.getSongTitle { title ->
            songTitle.value = title
            Log.d("Firestore", "Retrieved Title: ${songTitle.value}")
        }
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
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                    ){
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

                Spacer(modifier = Modifier.height(8.dp))

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
                Spacer(modifier = Modifier.height(8.dp))
                // Εμφάνιση στίχων με συγχορδίες
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 16.dp)
                ) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Spacer(modifier = Modifier.height(16.dp))

                            ChordText(songLine = song)
                        }
                    }
                }
                if (showDialog.value) {
                    AlertDialog(
                        onDismissRequest = { showDialog.value = false },
                        confirmButton = {
                            Text(
                                text = "OK",
                                modifier = Modifier.clickable { showDialog.value = false }
                            )
                        },
                        title = { Text("Επιλογές") },
                        text = { Text("Εδώ μπορείς να προσθέσεις επιλογές για το τραγούδι.") }
                    )
                }

            }
        }
    }
}
