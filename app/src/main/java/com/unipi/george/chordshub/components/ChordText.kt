package com.unipi.george.chordshub.components

import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextAlign


@Composable
fun ChordText(songLine: SongLine, onChordClick: (String) -> Unit) {
    Column {
        val lines = songLine.lyrics.split("\n")
        var currentIndex = 0

        lines.forEach { line ->
            val chordsInLine = songLine.chords.filter { chord ->
                chord.position >= currentIndex && chord.position < currentIndex + line.length
            }

            // Δημιουργία του AnnotatedString για συγχορδίες
            val chordLine = buildAnnotatedString {
                var currentPos = 0

                chordsInLine.forEach { chord ->
                    val relativePosition = chord.position - currentIndex
                    while (currentPos < relativePosition) {
                        append(" ")
                        currentPos++
                    }
                    pushStringAnnotation(tag = "chord", annotation = chord.chord)
                    withStyle(style = SpanStyle(color = Color.Red, fontSize = 14.sp)) {
                        append("${chord.chord} ")
                    }
                    pop()
                    currentPos += chord.chord.length + 1
                }
            }

            // Προβολή συγχορδιών με ClickableText
            ClickableText(
                text = chordLine,
                style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Start),
                modifier = Modifier.fillMaxWidth(),
                onClick = { offset ->
                    chordLine.getStringAnnotations("chord", offset, offset)
                        .firstOrNull()?.let { annotation ->
                            onChordClick(annotation.item)
                        }
                }
            )

            // Προβολή στίχων
            Text(
                text = line,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 24.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            currentIndex += line.length + 1
        }
    }
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

