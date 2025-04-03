package com.unipi.george.chordshub.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.unipi.george.chordshub.viewmodels.user.SettingsViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

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
fun AppText(text: String, settingsViewModel: SettingsViewModel, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = TextStyle(fontSize = settingsViewModel.fontSize.value.sp),
        modifier = modifier
    )
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
