package com.unipi.george.chordshub.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import com.unipi.george.chordshub.components.SongLine
import com.unipi.george.chordshub.components.ChordPosition
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.unit.sp
import com.unipi.george.chordshub.components.ChordText

import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Slider


import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import androidx.compose.ui.platform.LocalContext
@Composable
fun HomeScreen(navController: NavController, isFullScreen: Boolean, onFullScreenChange: (Boolean) -> Unit) {
    val songTitles = listOf("Song 1", "Song 2", "Song 3", "Song 4", "Song 5", "Song 6", "Song 7", "Song 8", "Song 9", "Song 10", "Song 11", "Song 12")
    val selectedSong = remember { mutableStateOf<SongLine?>(null) }

    if (selectedSong.value == null) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(songTitles) { title ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedSong.value = getSongData(title)
                        },
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    } else {
        DetailedSongView(
            song = selectedSong.value!!,
            isFullScreen = isFullScreen,
            onFullScreenChange = onFullScreenChange
        ) {
            selectedSong.value = null
            onFullScreenChange(false) // Όταν πατάει back, φεύγει από fullscreen
        }
    }
}

fun getSongData(title: String): SongLine {
    return SongLine(
        lyrics = "Em                 Am            Em\n" +
                "People are strange when you're a stranger\n" +
                "Am         Em    B7          Em\n" +
                "Faces look ugly when you're alone\n" +
                "                   Am           Em\n" +
                "Women seem wicked when you're unwanted\n" +
                "   Am         Em         B7               Em\n" +
                "Streets are uneven          when you're down\n" +
                " \n" +
                " \n" +
                "[Chorus 1]\n" +
                " \n" +
                "               B7\n" +
                "When you're strange\n" +
                " G                         B7\n" +
                "Faces come out of the rain\n" +
                " \n" +
                "When you're strange\n" +
                "G                           B7\n" +
                "No one remembers your name\n" +
                "B7\n" +
                "When you're strange\n" +
                "B7\n" +
                "When you're strange\n" +
                "B7\n" +
                "When you're strange\n" +
                " \n" +
                " \n" +
                "[Verse 2]\n" +
                " \n" +
                "Em                 Am            Em\n" +
                "People are strange when you're a stranger\n" +
                "Am         Em    B7           Em\n" +
                "Faces look ugly when you're alone\n" +
                "                   Am           Em\n" +
                "Women seem wicked when you're unwanted\n" +
                "   Am         Em         B7               Em\n" +
                "Streets are uneven          when you're down\n" +
                " \n" +
                " \n" +
                "[Guitar Solo]\n" +
                " \n" +
                "B7 Em B7 Em\n" +
                " \n" +
                " \n" +
                "[Chorus 2]\n" +
                " \n" +
                "               B7\n" +
                "When you're strange\n" +
                " G                         B7\n" +
                "Faces come out of the rain\n" +
                " \n" +
                "When you're strange\n" +
                "G                          B7\n" +
                "No one remembers your name\n" +
                "B7\n" +
                "When you're strange\n" +
                "B7\n" +
                "When you're strange\n" +
                "B7\n" +
                "When you're strange\n" +
                " \n" +
                "Alright, Yeah.\n" +
                " \n" +
                " \n" +
                "[Keyboard solo]\n" +
                " \n" +
                "Em Am Em Am Em B7 Em\n" +
                "Em Am Em Am Em B7 Em\n" +
                " \n" +
                " \n" +
                "[Chorus 3]\n" +
                " \n" +
                "            B7\n" +
                "When you're strange\n" +
                "G                          B7\n" +
                "Faces come out of the rain\n" +
                "               B7\n" +
                "When you're strange\n" +
                " G                         B7\n" +
                "No one remembers your name\n" +
                "B7\n" +
                "When you're strange\n" +
                "B7\n" +
                "When you're strange\n" +
                "B7           B7\n" +
                "When you're  strange",
        chords = listOf(
            ChordPosition(chord = "Em", position = 0),
            ChordPosition(chord = "Am", position = 17),
            ChordPosition(chord = "Em", position = 30)
        )
    )
}




@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DetailedSongView(song: SongLine, isFullScreen: Boolean, onFullScreenChange: (Boolean) -> Unit, onBack: () -> Unit) {
    val showDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current // **Προσθήκη του context**
    val isScrolling = remember { mutableStateOf(false) }
    val scrollSpeed = remember { mutableStateOf(100f) }
    val isSpeedControlVisible = remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

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
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Song Details",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = if (isFullScreen) 18.sp else 16.sp
                    )

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

                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 16.dp)
                ) {
                    item {
                        ChordText(songLine = song)
                    }
                }
            }
        }
    }

    // AlertDialog για τις ρυθμίσεις
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("Settings") },
            text = {
                Column {
                    Button(
                        onClick = {
                            saveCardContentAsPdf(context, "Saved_Song", listOf(song)) // **Κλήση της saveCardContentAsPdf**
                            showDialog.value = false // **Κλείσιμο του διαλόγου μετά την αποθήκευση**
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save as PDF")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { showDialog.value = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Close")
                    }
                }
            },
            confirmButton = { /* Χωρίς επιβεβαίωση γιατί έχουμε ξεχωριστά κουμπιά */ }
        )
    }
}




fun saveCardContentAsPdf(context: android.content.Context, title: String, lyrics: List<SongLine>) {
    val pdfDocument = android.graphics.pdf.PdfDocument()

    val pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = pdfDocument.startPage(pageInfo)

    val canvas = page.canvas
    val paint = android.graphics.Paint()

    // Σχεδίαση Τίτλου
    paint.textSize = 20f
    paint.isFakeBoldText = true
    canvas.drawText(title, 50f, 50f, paint)

    // Σχεδίαση Στίχων με Συγχορδίες
    paint.textSize = 16f
    paint.isFakeBoldText = false
    var yPosition = 100f
    lyrics.forEach { line ->
        canvas.drawText(line.lyrics, 50f, yPosition, paint)
        yPosition += 20f
        line.chords.forEach { chord ->
            canvas.drawText(" - ${chord.chord} at ${chord.position}", 70f, yPosition, paint)
            yPosition += 20f
        }
        yPosition += 10f
    }

    pdfDocument.finishPage(page)

    // Αποθήκευση στο φάκελο Downloads
    val downloadsDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS)
    val file = java.io.File(downloadsDir, "$title.pdf")
    try {
        pdfDocument.writeTo(java.io.FileOutputStream(file))


        val uri = android.net.Uri.fromFile(file)
        val intent = android.content.Intent(android.content.Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        intent.data = uri
        context.sendBroadcast(intent)


        Toast.makeText(context, "PDF saved to Downloads: ${file.absolutePath}", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Error saving PDF: ${e.message}", Toast.LENGTH_LONG).show()
    } finally {
        pdfDocument.close()
    }
}

