package com.unipi.george.chordshub.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import com.unipi.george.chordshub.models.SongLine
import com.unipi.george.chordshub.viewmodels.main.HomeViewModel
import androidx.compose.material3.MaterialTheme

@Composable
fun CardsView(
    songList: List<Pair<String, String>>,
    homeViewModel: HomeViewModel,
    selectedTitle: MutableState<String?>
) {
    val colors = listOf(
        Color(0xFFEF9A9A), // Light Red
        Color(0xFF90CAF9), // Light Blue
        Color(0xFFA5D6A7), // Light Green
        Color(0xFFFFF59D), // Light Yellow
        Color(0xFFCE93D8)  // Light Purple
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.Transparent),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(songList) { index, (title, songId) ->
            val backgroundColor = colors[index % colors.size] // Κυκλική επιλογή χρώματος
            SongCard(title, songId, homeViewModel, selectedTitle, backgroundColor)
        }
    }
}

@Composable
fun SongCard(
    title: String,
    songId: String,
    homeViewModel: HomeViewModel,
    selectedTitle: MutableState<String?>,
    backgroundColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                Log.d("HomeScreen", "Selected song ID: $songId")
                homeViewModel.selectSong(songId)
                selectedTitle.value = title
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black // Καλή αντίθεση με τα διαφορετικά background
            )
        }
    }
}



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

