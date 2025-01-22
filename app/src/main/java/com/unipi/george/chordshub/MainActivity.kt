package com.unipi.george.chordshub

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.unipi.george.chordshub.repository.AuthRepository.isUserLoggedIn
import com.unipi.george.chordshub.screens.HomeScreen
import com.unipi.george.chordshub.screens.LoginScreen
import com.unipi.george.chordshub.screens.SignUpScreen
import com.unipi.george.chordshub.ui.theme.ChordsHubTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChordsHubTheme {
                //SongScreen()
                val navController = rememberNavController()
                val startDestination = if (isUserLoggedIn()) "Home" else "Login"

                NavHost(navController = navController, startDestination = startDestination) {
                    composable("Login") { LoginScreen(navController) }
                    composable("SignUp") { SignUpScreen(navController) }
                    composable("Home") { HomeScreen(navController) }
                }
            }
        }
    }
}

@Composable
fun SongScreen() {
    val lyrics = listOf(
        LyricLine(
            line = "Στο θολωμένο μου μυαλό",
            chords = listOf(
                ChordPosition(chord = "Am", position = 5),
                ChordPosition(chord = "C", position = 17)
            )
        ),
        LyricLine(
            line = "ο κόσμος είναι μια σταλιά",
            chords = listOf(
                ChordPosition(chord = "Dm", position = 0),
                ChordPosition(chord = "E", position = 16),
                ChordPosition(chord = "Am", position = 20)
            )
        )
    )

    val context = LocalContext.current

    LyricsWithChords(
        lyrics = lyrics,
        onChordClick = { chord ->
            Toast.makeText(context, "Κλικ στη συγχορδία: $chord", Toast.LENGTH_SHORT).show()
        }
    )
}

@Composable
fun LyricsWithChords(
    lyrics: List<LyricLine>,
    onChordClick: (String) -> Unit
) {
    LazyColumn {
        items(lyrics) { line ->
            Box(modifier = Modifier.padding(8.dp)) {
                TextWithChords(line = line, onChordClick = onChordClick)
            }
        }
    }
}

@Composable
fun TextWithChords(
    line: LyricLine,
    onChordClick: (String) -> Unit
) {
    Box(modifier = Modifier.padding(8.dp) ) {
        // Εμφάνιση Στίχων
        Text(
            text = line.line,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.align(Alignment.CenterStart)
        )

        // Εμφάνιση Συγχορδιών
        line.chords.forEach { chord ->
            val chordOffset: Dp = with(LocalDensity.current) {
                val textBeforeChord = line.line.substring(0, chord.position)
                val textWidth = MaterialTheme.typography.bodyLarge.fontSize.toPx() * textBeforeChord.length * 0.6f
                textWidth.toDp()
            }

            Text(
                text = chord.chord,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.Red,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                ),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = chordOffset, y = (-20).dp) // Τοποθέτηση πάνω από τον στίχο
                    .clickable { onChordClick(chord.chord) }
            )
        }
    }
}

data class LyricLine(
    val line: String,
    val chords: List<ChordPosition>
)

data class ChordPosition(
    val chord: String,
    val position: Int
)
