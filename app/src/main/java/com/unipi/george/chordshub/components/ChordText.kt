package com.unipi.george.chordshub.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import com.unipi.george.chordshub.models.Chord
import com.unipi.george.chordshub.models.SongLine


@Composable
fun ChordText(songLine: SongLine, onChordClick: (String) -> Unit) {
    val text = songLine.text
    val chordsInLine = songLine.chords.sortedBy { it.position }

    // Δημιουργούμε το AnnotatedString
    val annotatedString = buildAnnotatedString {
        var currentPos = 0

        chordsInLine.forEach { chord ->
            val relativePosition = chord.position.coerceAtMost(text.length)

            // Προσθήκη κενού χώρου μέχρι τη θέση της συγχορδίας
            while (currentPos < relativePosition) {
                append(" ")
                currentPos++
            }

            // Προσθήκη Clickable συγχορδίας
            pushStringAnnotation(tag = "chord", annotation = chord.chord)
            withStyle(style = SpanStyle(color = Color.Red)) {
                append(chord.chord)
            }
            pop()

            currentPos += chord.chord.length + 2
        }

        // Προσθήκη στίχων μετά τις συγχορδίες
        append("\n$text")
    }

    // Text με pointerInput για clickable συγχορδίες
    BasicText(
        text = annotatedString,
        style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Start),
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures { tapOffset: Offset ->
                val offsetInt = tapOffset.x.toInt() // Παίρνουμε μόνο το x (οριζόντια θέση)

                val clickedAnnotations = annotatedString.getStringAnnotations(
                    tag = "chord",
                    start = offsetInt,
                    end = offsetInt
                )

                if (clickedAnnotations.isNotEmpty()) {
                    onChordClick(clickedAnnotations.first().item)
                }
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

        ChordText(
            songLine = song,
            onChordClick = { chordName ->
                selectedChord = fetchChordDetails(chordName)
                showDialog = true
            }
        )

        selectedChord?.let { chord ->
            if (showDialog) {
                ChordDialog(chord = chord, onDismiss = { showDialog = false })
            }
        }
    }
}

