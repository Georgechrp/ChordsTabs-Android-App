package com.unipi.george.chordshub.components

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Snackbar
import androidx.compose.ui.Alignment
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
            withStyle(style = SpanStyle(color = Color.Red, fontSize = 18.sp)) {
                append(chord.chord)
            }
            pop()

            currentPos += chord.chord.length + 1
        }

        // Προσθήκη στίχων μετά τις συγχορδίες
        append("\n$text")
    }

    // Χρησιμοποιούμε `ClickableText` για πιο αξιόπιστο χειρισμό των taps
    ClickableText(
        text = annotatedString,
        style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Start),
        modifier = Modifier.fillMaxWidth().padding(4.dp),
        onClick = { offset ->
            val clickedAnnotations = annotatedString.getStringAnnotations(
                tag = "chord",
                start = offset,
                end = offset
            )

            if (clickedAnnotations.isNotEmpty()) {
                onChordClick(clickedAnnotations.first().item)
            }
        }
    )
}

@Composable
fun SongDisplay(song: SongLine) {
    val snackbarHostState = remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        ChordText(
            songLine = song,
            onChordClick = { chordName ->
                snackbarHostState.value = "Επιλέξατε: $chordName"
            }
        )

        snackbarHostState.value?.let { message ->
            Snackbar(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                action = {
                    Text("OK", modifier = Modifier.clickable { snackbarHostState.value = null })
                }
            ) {
                Text(message)
            }
        }
    }
}
