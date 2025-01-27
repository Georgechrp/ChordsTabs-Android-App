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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.runtime.*

data class ChordPosition(
    val chord: String,
    val position: Int
)

data class SongLine(
    val lyrics: String,
    val chords: List<ChordPosition>
)

@Composable
fun ChordText(songLine: SongLine) {
    val context = LocalContext.current
    var selectedChord by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Εμφάνιση συγχορδιών πάνω από το κείμενο
        Row(modifier = Modifier.fillMaxWidth()) {
            var currentIndex = 0
            songLine.chords.forEach { chord ->
                val adjustedPosition = chord.position - 1 // Adjust για την αρχή της μέτρησης
                val space = adjustedPosition - currentIndex
                if (space > 0) {
                    Spacer(modifier = Modifier.width(space.dp * 6)) // Ρύθμιση multiplier
                }
                ClickableText(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = Color.Red, fontSize = 14.sp)) {
                            append(chord.chord)
                        }
                    },
                    modifier = Modifier.padding(4.dp), // Προσθήκη padding
                    onClick = {
                        Log.d("ChordClick", "Clicked on chord: ${chord.chord}")
                        Toast.makeText(context, "Clicked on chord: ${chord.chord}", Toast.LENGTH_SHORT).show()
                        selectedChord = chord.chord
                    }
                )
                currentIndex = adjustedPosition
            }
        }
        // Εμφάνιση στίχων
        Text(
            text = songLine.lyrics,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 4.dp)
        )
    }

    // Εμφάνιση διαλόγου όταν επιλέγεται μια συγχορδία
    selectedChord?.let {
        ChordDialog(chordName = it) {
            selectedChord = null
        }
    }
}
@Composable
fun ChordDialog(chordName: String, onDismiss: () -> Unit) {
    val chordData = mapOf(
        "B7" to "e|---2---|\nB|---0---|\nG|---2---|\nD|---1---|\nA|---2---|\nE|-------|",
        "C" to "e|---0---|\nB|---1---|\nG|---0---|\nD|---2---|\nA|---3---|\nE|-------|"
    )

    val chordDiagram = chordData[chordName] ?: "Chord not found"

    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(onClick = { onDismiss() }) {
                Text("Κλείσιμο")
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Συγχορδία: $chordName",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = chordDiagram,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    )
}


@Composable
fun ChordWebView(chordName: String) {
    val url = "https://chordpic.com/api/svg/$chordName"

    AndroidView(factory = { context ->
        WebView(context).apply {
            settings.javaScriptEnabled = true
            Log.d("ChordWebView", "Loading chord: $url")
            loadUrl(url)
        }
    }, modifier = Modifier
        .fillMaxWidth()
        .height(300.dp))
}
