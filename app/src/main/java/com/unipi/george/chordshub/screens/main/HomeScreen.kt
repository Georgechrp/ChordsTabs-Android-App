package com.unipi.george.chordshub.screens.main

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.unipi.george.chordshub.components.MyAppTopBar
import com.unipi.george.chordshub.components.FilterRow
import com.unipi.george.chordshub.screens.seconds.DetailedSongView
import com.unipi.george.chordshub.viewmodels.main.HomeViewModel
import com.unipi.george.chordshub.viewmodels.MainViewModel
import com.unipi.george.chordshub.viewmodels.user.UserViewModel
import kotlin.math.roundToInt

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    mainViewModel: MainViewModel,
    userViewModel: UserViewModel,
    navController: NavController,
    painter: Painter,
    onMenuClick: () -> Unit
) {
    val selectedSongId by homeViewModel.selectedSongId.collectAsState()
    val songList by homeViewModel.songList.collectAsState()
    var selectedFilter by remember { mutableStateOf("All") }
    val selectedTitle = remember { mutableStateOf<String?>(null) }

    val isMenuOpen by mainViewModel.isMenuOpen
    val isFullScreenState by homeViewModel.isFullScreen.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val scrollState = rememberScrollState()

    // Κρατάμε την τρέχουσα θέση του top bar
    var topBarOffset by rememberSaveable { mutableFloatStateOf(0f) }

    // Nested Scroll για σωστό collapsing behavior
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val newOffset = (topBarOffset + available.y).coerceIn(-30f, 0f) // TopBar moves up to -dp
                topBarOffset = newOffset
                return Offset.Zero
            }
        }
    }

    LaunchedEffect(navBackStackEntry) {
        homeViewModel.fetchFilteredSongs("All")
    }

    LaunchedEffect(selectedFilter) {
        homeViewModel.fetchFilteredSongs(selectedFilter)
    }



    BackHandler(enabled = isMenuOpen) {
        mainViewModel.setMenuOpen(false)
    }

    Scaffold(containerColor = Color.Transparent,
        topBar = {
            if (selectedSongId == null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset { IntOffset(x = 0, y = topBarOffset.roundToInt()) }
                        .background(Color.Transparent)
                ) {
                    MyAppTopBar(painter = painter, onMenuClick = onMenuClick) {
                        Column {
                            FilterRow(
                                selectedFilter = selectedFilter,
                                onFilterChange = { selectedFilter = it }
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues).nestedScroll(nestedScrollConnection).background(Color.Transparent)) {
            when {

                selectedSongId == null && songList.isEmpty() -> LoadingView()
                selectedSongId == null -> CardsView(songList, homeViewModel, selectedTitle, scrollState)
                else -> DetailedSongView(
                    songId = selectedSongId!!,
                    isFullScreenState = isFullScreenState,
                    onBack = {
                        homeViewModel.clearSelectedSong()
                        homeViewModel.setFullScreen(false)
                    },
                    navController = navController,
                    mainViewModel = mainViewModel,
                    homeViewModel = homeViewModel,
                    userViewModel = userViewModel
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
    homeViewModel: HomeViewModel,
    selectedTitle: MutableState<String?>,
    scrollState: androidx.compose.foundation.ScrollState
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize().padding(16.dp).background(Color.Transparent).scrollable(scrollState, orientation = androidx.compose.foundation.gestures.Orientation.Vertical),

        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ){
        items(songList) { (title, songId) ->
            SongCard(title, songId, homeViewModel, selectedTitle)
        }
    }
}


@Composable
fun SongCard(
    title: String,
    songId: String,
    homeViewModel: HomeViewModel,
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



