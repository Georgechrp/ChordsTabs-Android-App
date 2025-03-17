package com.unipi.george.chordshub.screens.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.unipi.george.chordshub.components.MyAppTopBar
import com.unipi.george.chordshub.components.FilterRow
import com.unipi.george.chordshub.screens.seconds.DetailedSongView
import com.unipi.george.chordshub.viewmodels.main.HomeViewModel
import com.unipi.george.chordshub.viewmodels.MainViewModel
import com.unipi.george.chordshub.viewmodels.user.UserViewModel
import kotlin.math.roundToInt
import com.unipi.george.chordshub.components.CardsView
import com.unipi.george.chordshub.components.LoadingView

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
                    MyAppTopBar(mainViewModel, onMenuClick = onMenuClick) {
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





