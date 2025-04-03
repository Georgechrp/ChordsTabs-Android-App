package com.unipi.george.chordshub.screens.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.unipi.george.chordshub.R
import com.unipi.george.chordshub.components.MyAppTopBar
import com.unipi.george.chordshub.components.FilterRow
import com.unipi.george.chordshub.screens.viewsong.DetailedSongView
import com.unipi.george.chordshub.viewmodels.main.HomeViewModel
import com.unipi.george.chordshub.viewmodels.MainViewModel
import com.unipi.george.chordshub.viewmodels.user.UserViewModel
import kotlin.math.roundToInt
import com.unipi.george.chordshub.components.CardsView
import com.unipi.george.chordshub.components.LoadingView
import kotlinx.coroutines.delay

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
    var topBarOffset by rememberSaveable { mutableFloatStateOf(0f) } // Κρατάμε την τρέχουσα θέση του top br
    val profileImage by mainViewModel.profileImageUrl.collectAsState()
    val genreFilters = listOf("All", "Pop", "Hip-Hop", "R&B", "Reggae")
    val moodFilters = listOf("Happy", "Sad", "Chill", "Energetic")
    var showNoResults by remember { mutableStateOf(false) }


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
    LaunchedEffect(songList, selectedFilter) {
        showNoResults = false
        if (songList.isEmpty()) {
            delay(5000)
            if (songList.isEmpty()) {
                showNoResults = true
            }
        }
    }

    BackHandler(enabled = isMenuOpen) {
        mainViewModel.setMenuOpen(false)
    }

    Scaffold(containerColor = Color.Transparent,
        topBar = {
            if (selectedSongId == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset { IntOffset(x = 0, y = topBarOffset.roundToInt()) }
                        .zIndex(1f) // Για σιγουριά ότι είναι πάνω απ’ το scrollable περιεχόμενο
                ) {
                    MyAppTopBar(
                        imageUrl = profileImage,
                        onMenuClick = onMenuClick
                    ) {
                        FilterRow(
                            selectedFilter = selectedFilter,
                            onFilterChange = { selectedFilter = it },
                            filters = genreFilters + moodFilters
                        )
                    }
                }
            }
        }

    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(2.dp)
            .nestedScroll(nestedScrollConnection)
            .background(Color.Transparent))
        {
            when {
                selectedSongId == null && songList.isEmpty() && showNoResults -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = stringResource(R.string.unfortunately_text),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
                selectedSongId == null && songList.isEmpty() -> LoadingView()
                selectedSongId == null -> CardsView(songList, homeViewModel, selectedTitle)
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





