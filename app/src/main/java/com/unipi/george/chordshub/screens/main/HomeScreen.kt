package com.unipi.george.chordshub.screens.main

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.unipi.george.chordshub.components.AppTopBar
import com.unipi.george.chordshub.components.FilterRow
import com.unipi.george.chordshub.repository.FirestoreRepository
import com.unipi.george.chordshub.screens.seconds.DetailedSongView
import com.unipi.george.chordshub.viewmodels.HomeViewModel

@Composable
fun HomeScreen(
    isFullScreen: Boolean,
    onFullScreenChange: (Boolean) -> Unit,
    homeViewModel: HomeViewModel,
    navController: NavController,
    painter: Painter,
    onMenuClick: () -> Unit
) {
    val repository = remember { FirestoreRepository(FirebaseFirestore.getInstance()) }
    val selectedSongId by homeViewModel.selectedSongId.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var songList by remember { mutableStateOf(emptyList<Pair<String, String>>()) }
    var selectedFilter by remember { mutableStateOf("All") }
    val selectedTitle = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(selectedFilter) {
        repository.getFilteredSongs(selectedFilter) { titlesAndIds ->
            songList = titlesAndIds
        }
    }

    Scaffold(
        topBar = {
            if (selectedSongId == null) {
                AppTopBar(
                    painter = painter,
                    onMenuClick = onMenuClick
                ) {
                    Column {

                        FilterRow(
                            selectedFilter = selectedFilter,
                            onFilterChange = { selectedFilter = it }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when {
                selectedSongId == null && songList.isEmpty() -> LoadingView()
                selectedSongId == null -> CardsView(songList, repository, homeViewModel, coroutineScope, selectedTitle)
                else -> DetailedSongView(
                    songId = selectedSongId!!,
                    isFullScreen = isFullScreen,
                    onFullScreenChange = onFullScreenChange,
                    onBack = {
                        homeViewModel.clearSelectedSong()
                        onFullScreenChange(false)
                    },
                    repository = repository,
                    navController = navController
                )
            }
        }
    }
}



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
fun CardsView(
    songList: List<Pair<String, String>>,
    repository: FirestoreRepository,
    homeViewModel: HomeViewModel,
    coroutineScope: kotlinx.coroutines.CoroutineScope,
    selectedTitle: MutableState<String?>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(songList) { (title, songId) ->
            SongCard(title, songId, repository, homeViewModel, coroutineScope, selectedTitle)
        }
    }
}

@Composable
fun SongCard(
    title: String,
    songId: String,
    repository: FirestoreRepository,
    homeViewModel: HomeViewModel,
    coroutineScope: kotlinx.coroutines.CoroutineScope,
    selectedTitle: MutableState<String?>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                Log.d("HomeScreen", "Selected song ID: $songId")
                homeViewModel.selectSong(songId)
                selectedTitle.value = title
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



