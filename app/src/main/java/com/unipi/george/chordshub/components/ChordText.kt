package com.unipi.george.chordshub.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.ClickableText
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign

data class ChordPosition(
    val chord: String,
    val position: Int
)

data class SongLine(
    val lyrics: String,
    val chords: List<ChordPosition>
)

data class Chord(
    val chordName: String,
    val positions: String,
    val fingers: String
)

@Composable
fun ChordText(songLine: SongLine) {
    Column {
        val lines = songLine.lyrics.split("\n")
        var currentIndex = 0

        lines.forEach { line ->
            // Φιλτράρουμε τις συγχορδίες που ανήκουν σε αυτή τη γραμμή
            val chordsInLine = songLine.chords.filter {
                it.position in currentIndex until currentIndex + line.length
            }

            val chordLine = buildAnnotatedString {
                var currentPos = 0

                chordsInLine.forEach { chord ->
                    val relativePosition = chord.position - currentIndex
                    while (currentPos < relativePosition) {
                        append(" ")
                        currentPos++
                    }
                    withStyle(style = SpanStyle(color = Color.Red, fontSize = 14.sp)) {
                        append("${chord.chord} ")
                    }
                    currentPos += chord.chord.length + 1
                }
            }

            ClickableText(
                text = chordLine,
                style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Start),
                modifier = Modifier.fillMaxWidth(),
                onClick = { offset ->
                    chordsInLine.forEach { chord ->
                        val chordStart = chord.position - currentIndex
                        val chordEnd = chordStart + chord.chord.length
                        if (offset in chordStart..chordEnd) {
                            // onChordClick(chord.chord)
                        }
                    }
                }
            )

            Text(
                text = line,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 24.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            // Ενημερώνουμε το currentIndex για την επόμενη γραμμή
            currentIndex += line.length + 1 // +1 για τον χαρακτήρα '\n'
        }
    }
}

@Composable
fun ChordDialog(chord: Chord, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(onClick = { onDismiss() }) {
                Text("Close")
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Chord: ${chord.chordName}",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Fret Positions: ${chord.positions}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black
                )
                Text(
                    text = "Finger Positions: ${chord.fingers}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    )
}

@Composable
fun SongDisplay(song: SongLine) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedChord by remember { mutableStateOf<Chord?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Display chords and lyrics
        ChordText(
            songLine = song,
            /*onChordClick = { chordName ->
                selectedChord = fetchChordDetails(chordName)
                showDialog = true
            }*/
        )

        // Show chord details dialog
        selectedChord?.let { chord ->
            if (showDialog) {
                ChordDialog(chord = chord, onDismiss = { showDialog = false })
            }
        }
    }
}

fun fetchChordDetails(chordName: String): Chord? {
    val chordDatabase = mapOf(
        "Em" to Chord("Em", "0 2 2 0 0 0", "X 2 3 1 1 1"),
        "Am" to Chord("Am", "X 0 2 2 1 0", "X 1 3 2 1 1"),
        "C" to Chord("C", "X 3 2 0 1 0", "X 3 2 0 1 0"),
        "G" to Chord("G", "3 2 0 0 0 3", "2 1 0 0 0 3"),
        "D" to Chord("D", "X X 0 2 3 2", "0 0 0 1 3 2")
    )
    return chordDatabase[chordName]
}