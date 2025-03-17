package com.unipi.george.chordshub.screens.seconds

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
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import com.unipi.george.chordshub.components.ChordText
import com.unipi.george.chordshub.models.SongLine
import com.unipi.george.chordshub.models.SongData
import com.unipi.george.chordshub.repository.FirestoreRepository
import com.unipi.george.chordshub.utils.saveCardContentAsPdf
import android.content.Context
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.unipi.george.chordshub.R
import com.unipi.george.chordshub.repository.AuthRepository
import com.unipi.george.chordshub.sharedpreferences.TransposePreferences
import com.unipi.george.chordshub.utils.QRCodeDialog
import com.unipi.george.chordshub.viewmodels.main.HomeViewModel
import com.unipi.george.chordshub.viewmodels.MainViewModel
import com.unipi.george.chordshub.viewmodels.user.UserViewModel


@Composable
fun DetailedSongView(
    songId: String,
    isFullScreenState: Boolean,
    onBack: () -> Unit,
    repository: FirestoreRepository = FirestoreRepository(FirebaseFirestore.getInstance()),
    navController: NavController,
    mainViewModel: MainViewModel,
    homeViewModel: HomeViewModel,
    userViewModel: UserViewModel
) {
    val songDataState = remember { mutableStateOf<SongData?>(null) }
    val transposeValue = remember { mutableStateOf(0) }
    val isScrolling = remember { mutableStateOf(false) }
    val scrollSpeed = remember { mutableFloatStateOf(30f) }
    val isSpeedControlVisible = remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val showDialog = remember { mutableStateOf(false) }
    val showQRCodeDialog = remember { mutableStateOf(false) }


    val context = LocalContext.current
    val transposePreferences = remember { TransposePreferences(context) }


    val userId = AuthRepository.getUserId()

    LaunchedEffect(isScrolling.value) {
        while (isScrolling.value) {
            listState.animateScrollBy(scrollSpeed.floatValue)
            kotlinx.coroutines.yield()
        }
    }

    LaunchedEffect(songId) {
        transposeValue.value = transposePreferences.getTransposeValue(songId)
        Log.d("DetailedSongView", "LaunchedEffect triggered for songId: $songId")
        val songData = repository.getSongDataAsync(songId)
        songDataState.value = songData
        // Καταγραφή του τραγουδιού στη λίστα recentSongs
        userId?.let { id ->
            userViewModel.addRecentSong(id, songData?.title ?: "Untitled")
        }
    }

    fun applyTranspose() {
        songDataState.value = songDataState.value?.copy(
            lyrics = songDataState.value?.lyrics?.map { line ->
                line.copy(
                    chords = line.chords.map { chord ->
                        chord.copy(chord = getNewKey(chord.chord, transposeValue.value))
                    }
                )
            }
        )
        transposePreferences.saveTransposeValue(songId, transposeValue.value)
    }

    BackHandler {
        if (isFullScreenState) {
            homeViewModel.setFullScreen(false)
        } else {
            onBack()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                if (isFullScreenState) {
                    homeViewModel.setFullScreen(false)
                } else {
                    homeViewModel.setFullScreen(true)
                }
            }
    )
    {
        if (songDataState.value == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Loading...")
            }
        } else {
            val songData = songDataState.value!!

            Card(
                modifier = if (isFullScreenState) Modifier.fillMaxSize() else Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(if (isFullScreenState) 0.dp else 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isFullScreenState) 0.dp else 8.dp)
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
                        SongInfoPlace(
                            title = songData.title ?: "No Title",
                            artist = songData.artist ?: "Unknown Artist",
                            isFullScreen = isFullScreenState,
                            navController ,
                            modifier = Modifier.weight(1f)
                        )
                        OptionsPlace(
                            isScrolling = isScrolling,
                            isSpeedControlVisible = isSpeedControlVisible,
                            showDialog = showDialog,
                            showQRCodeDialog = showQRCodeDialog
                        )

                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    ControlSpeed(scrollSpeed, isSpeedControlVisible)

                    Spacer(modifier = Modifier.height(8.dp))

                    SongLyricsView(songLines = songData.lyrics ?: emptyList(), listState = listState)

                    OptionsDialog(
                        showDialog = showDialog,
                        transposeValue = transposeValue,
                        onTransposeChange = {
                            applyTranspose()
                        },
                        context = LocalContext.current,
                        songTitle = songDataState.value?.title ?: "Untitled",
                        songLyrics = songDataState.value?.lyrics ?: emptyList()
                    )
                    QRCodeDialog(showQRCodeDialog, songId)
                }
            }
        }
    }
}




@Composable
fun SongInfoPlace(
    title: String,
    artist: String,
    isFullScreen: Boolean,
    navController: NavController,
    modifier: Modifier = Modifier
) {
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
                color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.clickable {
                navController.navigate("artist/$artist")
            }
        )
    }
}



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OptionsPlace(
    isScrolling: MutableState<Boolean>,
    isSpeedControlVisible: MutableState<Boolean>,
    showDialog: MutableState<Boolean>,
    showQRCodeDialog: MutableState<Boolean>
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

        Box(
            modifier = Modifier
                .clickable { showQRCodeDialog.value = true }
                .padding(3.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.generateqrcode),
                contentDescription = "Share via QR",
                modifier = Modifier.size(24.dp)
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
                valueRange = 10f..100f,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun SongLyricsView(songLines: List<SongLine>, listState: LazyListState) {
    val snackbarHostState = remember { mutableStateOf<String?>(null) }

    Box {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp)
        ) {
            items(songLines) { line ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    ChordText(songLine = line, onChordClick = { clickedChord ->
                        snackbarHostState.value = "Επιλέξατε: $clickedChord"
                    })
                }
            }
        }

        snackbarHostState.value?.let { message ->
            Snackbar(
                modifier = Modifier.align(Alignment.BottomCenter),
                action = {
                    Text("OK", modifier = Modifier.clickable { snackbarHostState.value = null })
                }
            ) {
                Text(message)
            }
        }
    }
}

@Composable
fun OptionsDialog(
    showDialog: MutableState<Boolean>,
    transposeValue: MutableState<Int>,
    onTransposeChange: () -> Unit,
    context: Context,
    songTitle: String,
    songLyrics: List<SongLine>
) {
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("Transpose Options") },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Transpose by:", fontSize = 16.sp, modifier = Modifier.padding(bottom = 8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                if (transposeValue.value > -11) {
                                    transposeValue.value -= 1
                                    onTransposeChange()
                                }
                            },
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text("🔽")
                        }

                        TextField(
                            value = transposeValue.value.toString(),
                            onValueChange = {},
                            singleLine = true,
                            readOnly = true,
                            modifier = Modifier.width(80.dp)
                        )

                        Button(
                            onClick = {
                                if (transposeValue.value < 11) {
                                    transposeValue.value += 1
                                    onTransposeChange()
                                }
                            },
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text("🔼")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            saveCardContentAsPdf(context, songTitle, songLyrics)
                            showDialog.value = false
                        },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("📄 Save as PDF")
                    }
                }
            },
            confirmButton = {
                Text(
                    text = "OK",
                    modifier = Modifier.clickable { showDialog.value = false }
                )
            }
        )
    }
}


fun getNewKey(originalKey: String, transpose: Int): String {
    val notes = listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")

    val regex = Regex("^([A-Ga-g#b]+)(.*)$")
    val matchResult = regex.matchEntire(originalKey) ?: return originalKey

    val (rootNote, suffix) = matchResult.destructured
    val normalizedRoot = rootNote.replace("b", "#") // Αντικατάσταση "b" με "#"

    val index = notes.indexOfFirst { it.equals(normalizedRoot, ignoreCase = true) }
    if (index == -1) return originalKey // Αν δεν βρεθεί, επιστρέφουμε το ίδιο

    val newIndex = (index + transpose + notes.size) % notes.size
    return notes[newIndex] + suffix
}