package com.unipi.george.chordshub.screens.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import com.google.firebase.firestore.FirebaseFirestore
import com.unipi.george.chordshub.components.SongLine
import com.unipi.george.chordshub.repository.FirestoreRepository
import com.unipi.george.chordshub.viewmodels.HomeViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    isFullScreen: Boolean,
    onFullScreenChange: (Boolean) -> Unit,
    homeViewModel: HomeViewModel,
    selectedFilter: String
) {
    val repository = remember { FirestoreRepository(FirebaseFirestore.getInstance()) }
    val selectedSong = homeViewModel.selectedSong.collectAsState().value
    val selectedArtist = homeViewModel.selectedArtist.collectAsState().value
    val coroutineScope = rememberCoroutineScope()
    var songTitles by remember { mutableStateOf<List<String>>(emptyList()) }
    var songIds by remember { mutableStateOf<List<String>>(emptyList()) }
    val selectedTitle = remember { mutableStateOf<String?>(null) }
    //var selectedFilter by remember { mutableStateOf("All") }

    LaunchedEffect(selectedFilter) {
        println("📌 Calling getFilteredSongs with filter: $selectedFilter")
        repository.getFilteredSongs(selectedFilter) { titlesAndIds ->
            songTitles = titlesAndIds.map { it.first }
            songIds = titlesAndIds.map { it.second }
        }
    }


    if (selectedSong == null && songTitles.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(modifier = Modifier.size(50.dp))
        }
    } else if (selectedSong == null) {
        CardsView(songTitles, songIds, repository, homeViewModel, coroutineScope, selectedTitle)
    } else {
        DetailedSongView(
            isFullScreen = isFullScreen,
            onFullScreenChange = onFullScreenChange,
            onBack = {
                homeViewModel.clearSelectedSong()
                onFullScreenChange(false)
            },
            repository = repository // Προαιρετικά, αν θέλεις να περάσεις το repository
        )
    }
}

@Composable
fun CardsView(
    songTitles: List<String>,
    songIds: List<String>,
    repository: FirestoreRepository,
    homeViewModel: HomeViewModel,
    coroutineScope: kotlinx.coroutines.CoroutineScope,
    selectedTitle: androidx.compose.runtime.MutableState<String?>
) {
    val defaultPadding = 16.dp

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(defaultPadding),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(songTitles.zip(songIds)) { index, (title, songId) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        repository.setSongId(songId)
                        selectedTitle.value = title
                        coroutineScope.launch {
                            // Ανάκτηση των δεδομένων ως SongData
                            val songData = repository.getSongDataAsync()
                            if (songData != null) {
                                // Μετατροπή σε SongLine (αν το UI σου απαιτεί SongLine)
                                val songLine = SongLine(
                                    lyrics = songData.lyrics?.joinToString("\n") ?: "",
                                    chords = songData.chords ?: emptyList()
                                )
                                // Εδώ ορίζεις το selectedSong και τον artist στο ViewModel
                                homeViewModel.setSelectedSong(songLine, songData.artist)
                            }
                        }
                    },
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

        }
    }

}
