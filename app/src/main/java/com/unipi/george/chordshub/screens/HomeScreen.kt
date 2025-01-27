package com.unipi.george.chordshub.screens


import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.navigation.NavController

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.tooling.preview.Preview
import com.unipi.george.chordshub.components.ChordText
import com.unipi.george.chordshub.components.SongLine
import com.unipi.george.chordshub.components.ChordPosition
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(navController: NavController) {
    val song = listOf(
        SongLine(
            lyrics = "People are strange when you're a stranger",
            chords = listOf(
                ChordPosition(chord = "Em", position = 0),
                ChordPosition(chord = "Am", position = 13),
                ChordPosition(chord = "Em", position = 26)
            )
        ),
        SongLine(
            lyrics = "Faces look ugly when you're alone",
            chords = listOf(
                ChordPosition(chord = "Am", position = 0),
                ChordPosition(chord = "Em", position = 12),
                ChordPosition(chord = "B7", position = 17),
                ChordPosition(chord = "Em", position = 28)
            )
        ),
        SongLine(
            lyrics = "Women seem wicked when you're unwanted",
            chords = listOf(
                ChordPosition(chord = "Em", position = 0),
                ChordPosition(chord = "Am", position = 13),
                ChordPosition(chord = "Em", position = 26)
            )
        ),
        SongLine(
            lyrics = "Streets are uneven when you're down",
            chords = listOf(
                ChordPosition(chord = "Am", position = 0),
                ChordPosition(chord = "Em", position = 12),
                ChordPosition(chord = "B7", position = 17),
                ChordPosition(chord = "Em", position = 28)
            )
        ),
        SongLine(
            lyrics = "People are strange when you're a stranger",
            chords = listOf(
                ChordPosition(chord = "Em", position = 0),
                ChordPosition(chord = "Am", position = 13),
                ChordPosition(chord = "Em", position = 26)
            )
        ),
        SongLine(
            lyrics = "Faces look ugly when you're alone",
            chords = listOf(
                ChordPosition(chord = "Am", position = 0),
                ChordPosition(chord = "Em", position = 12),
                ChordPosition(chord = "B7", position = 17),
                ChordPosition(chord = "Em", position = 28)
            )
        ),
        SongLine(
            lyrics = "Women seem wicked when you're unwanted",
            chords = listOf(
                ChordPosition(chord = "Em", position = 0),
                ChordPosition(chord = "Am", position = 13),
                ChordPosition(chord = "Em", position = 26)
            )
        ),
        SongLine(
            lyrics = "Streets are uneven when you're down",
            chords = listOf(
                ChordPosition(chord = "Am", position = 0),
                ChordPosition(chord = "Em", position = 12),
                ChordPosition(chord = "B7", position = 17),
                ChordPosition(chord = "Em", position = 28)
            )
        ),
        SongLine(
            lyrics = "People are strange when you're a stranger",
            chords = listOf(
                ChordPosition(chord = "Em", position = 0),
                ChordPosition(chord = "Am", position = 13),
                ChordPosition(chord = "Em", position = 26)
            )
        ),
        SongLine(
            lyrics = "Faces look ugly when you're alone",
            chords = listOf(
                ChordPosition(chord = "Am", position = 0),
                ChordPosition(chord = "Em", position = 12),
                ChordPosition(chord = "B7", position = 17),
                ChordPosition(chord = "Em", position = 28)
            )
        ),
        SongLine(
            lyrics = "Women seem wicked when you're unwanted",
            chords = listOf(
                ChordPosition(chord = "Em", position = 0),
                ChordPosition(chord = "Am", position = 13),
                ChordPosition(chord = "Em", position = 26)
            )
        ),
        SongLine(
            lyrics = "Streets are uneven when you're down",
            chords = listOf(
                ChordPosition(chord = "Am", position = 0),
                ChordPosition(chord = "Em", position = 12),
                ChordPosition(chord = "B7", position = 17),
                ChordPosition(chord = "Em", position = 28)
            )
        )
    )

    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    // Αυτόματη κύλιση
    LaunchedEffect(scrollState) {
        coroutineScope.launch {
            while (true) { // Loop για συνεχή κύλιση
                val maxScroll = scrollState.maxValue
                scrollState.animateScrollTo(
                    value = maxScroll,
                    animationSpec = tween(durationMillis = 20000, easing = LinearEasing) // 20 δευτερόλεπτα
                )
                delay(1000)
                scrollState.scrollTo(0)
            }
        }
    }

    // Πλαίσιο με κάρτα και κύλιση
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState) // Ενσωμάτωση του scrollState
        ) {
            song.forEach { line ->
                ChordText(songLine = line)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun ChordText(songLine: SongLine) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Εμφάνιση συγχορδιών πάνω από τους στίχους
        Row(modifier = Modifier.fillMaxWidth()) {
            var currentIndex = 0
            songLine.chords.forEach { chord ->
                val adjustedPosition = chord.position - 1
                val space = adjustedPosition - currentIndex
                if (space > 0) {
                    Spacer(modifier = Modifier.width(space.dp * 6))
                }
                Text(
                    text = chord.chord,
                    color = Color.Red, // Χρώμα συγχορδιών
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                currentIndex = adjustedPosition
            }
        }
        // Εμφάνιση στίχων
        Text(
            text = songLine.lyrics,
            fontSize = 16.sp,
            color = Color.Black, // Βεβαιωνόμαστε ότι οι στίχοι είναι μαύροι
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}