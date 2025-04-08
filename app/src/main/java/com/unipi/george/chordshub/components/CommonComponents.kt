package com.unipi.george.chordshub.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.unipi.george.chordshub.viewmodels.user.SettingsViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.unipi.george.chordshub.R

/*
*   Just some methods can be used by any place in the app
*/

@Composable
fun LoadingView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(modifier = Modifier.size(50.dp))
    }
}

@Composable
fun SettingsHeads(text: String, settingsViewModel: SettingsViewModel, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = TextStyle(fontSize = settingsViewModel.fontSize.value.sp),
        modifier = modifier
    )
}

fun getNewKey(originalKey: String, transpose: Int): String {
    val sharpNotes = listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
    val flatNotes = listOf("C", "Db", "D", "Eb", "E", "F", "Gb", "G", "Ab", "A", "Bb", "B")

    // Αναγνώριση της ρίζας και του υπολοίπου
    val regex = Regex("^([A-Ga-g#b]+)(.*)$")
    val matchResult = regex.matchEntire(originalKey) ?: return originalKey
    val (rootNote, suffix) = matchResult.destructured

    // Προσδιορισμός αν η συγχορδία είναι flat ή sharp
    val isFlat = rootNote.contains("b")
    val isSharp = rootNote.contains("#")

    // Εύρεση τρέχοντος index για την ρίζα
    val currentIndex = if (isFlat) flatNotes.indexOf(rootNote) else sharpNotes.indexOf(rootNote)
    if (currentIndex == -1) return originalKey // Επιστρέφουμε την αρχική αν δεν βρεθεί

    // Υπολογισμός του νέου index
    val newIndex = (currentIndex + transpose + 12) % 12
    val newRootNote = if (isFlat) flatNotes[newIndex] else sharpNotes[newIndex]

    return newRootNote + suffix // Διατήρηση του υπολοίπου της συγχορδίας
}

// Circular ImageView that can be clicked(used by TopBar)
@Composable
fun CircularImageViewSmall(
    imageUrl: String?,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(30.dp)
            .clip(CircleShape)
            .border(2.dp, Color.Gray, CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (imageUrl != null) {
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = stringResource(R.string.circular_image_description),
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.user_icon),
                contentDescription = stringResource(R.string.circular_image_description),
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}


/*
fun addDataForFurElise() {
    val repository = FirestoreRepository(FirebaseFirestore.getInstance())

    lifecycleScope.launch {
        val songData = SongData(
            title = "Für Elise",
            artist = "Ludwig van Beethoven",
            key = "A Minor",
            bpm = 75, // Μέτριο tempo
            genres = listOf("Classical"),
            createdAt = System.currentTimeMillis().toString(),
            creatorId = "admin",
            lyrics = listOf(
                SongLine(
                    lineNumber = 1,
                    text = "Für Elise, section 1 melody",
                    chords = listOf(ChordPosition("Am", 0), ChordPosition("E", 8), ChordPosition("G", 16), ChordPosition("C", 24))
                ),
                SongLine(
                    lineNumber = 2,
                    text = "Für Elise, section 2 transition",
                    chords = listOf(ChordPosition("F", 0), ChordPosition("C", 10), ChordPosition("G", 20), ChordPosition("E", 28))
                )
            )
        )

        repository.addSongData("fur_elise_beethoven", songData)
    }
}*/
