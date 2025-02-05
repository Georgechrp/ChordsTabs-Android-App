package com.unipi.george.chordshub.screens


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import com.google.firebase.firestore.FirebaseFirestore
import com.unipi.george.chordshub.repository.FirestoreRepository
import com.unipi.george.chordshub.viewmodels.HomeViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    isFullScreen: Boolean,
    onFullScreenChange: (Boolean) -> Unit,
    homeViewModel: HomeViewModel
) {
    val repository = remember { FirestoreRepository(FirebaseFirestore.getInstance()) }
    val selectedSong = homeViewModel.selectedSong.collectAsState().value
    val selectedArtist = homeViewModel.selectedArtist.collectAsState().value
    val coroutineScope = rememberCoroutineScope()

    var songTitles by remember { mutableStateOf<List<String>>(emptyList()) }
    var songIds by remember { mutableStateOf<List<String>>(emptyList()) }
    val selectedTitle = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        repository.getSongTitles { titlesAndIds ->
            songTitles = titlesAndIds.map { it.first }
            songIds = titlesAndIds.map { it.second }
        }
    }

    if (selectedSong == null) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(songTitles) { title ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val songId = songIds[songTitles.indexOf(title)]
                            repository.setSongId(songId)
                            selectedTitle.value = title
                            coroutineScope.launch {
                                getSongData(repository) { song, artist ->
                                    homeViewModel.setSelectedSong(song, artist)
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
    } else {
        DetailedSongView(
            title = selectedTitle.value ?: "Unknown Title",
            artist = selectedArtist ?: "Unknown Artist",
            song = selectedSong,
            isFullScreen = isFullScreen,
            onFullScreenChange = onFullScreenChange
        ) {
            homeViewModel.clearSelectedSong()
            onFullScreenChange(false)
        }
    }
}
