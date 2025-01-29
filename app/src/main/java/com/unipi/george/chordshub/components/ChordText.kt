package com.unipi.george.chordshub.components

import android.util.Log
import android.webkit.WebView
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.*
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
    var selectedChord by remember { mutableStateOf<Chord?>(null) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            songLine.chords.forEachIndexed { index, chordPosition ->
                Box(modifier = Modifier.weight(1f, fill = false)) {
                    ClickableText(
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    color = Color.Black,
                                    background = Color.LightGray,
                                    fontSize = 14.sp
                                )
                            ) {
                                append(chordPosition.chord)
                            }
                        },
                        modifier = Modifier.padding(horizontal = 2.dp),
                        onClick = {
                            val chordDetails = fetchChordDetails(chordPosition.chord)
                            if (chordDetails != null) {
                                selectedChord = chordDetails
                            }
                        }
                    )
                }
            }
        }

        Text(
            text = songLine.lyrics,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )
    }

    selectedChord?.let { chord ->
        ChordDialog(chord = chord) {
            selectedChord = null
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

