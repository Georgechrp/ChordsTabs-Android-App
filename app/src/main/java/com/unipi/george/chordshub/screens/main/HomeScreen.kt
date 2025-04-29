package com.unipi.george.chordshub.screens.main

import android.annotation.SuppressLint
import android.net.Uri
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
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.firebase.firestore.FirebaseFirestore
import com.unipi.george.chordshub.R
import com.unipi.george.chordshub.components.FilterRow
import com.unipi.george.chordshub.screens.viewsong.DetailedSongView
import com.unipi.george.chordshub.viewmodels.main.HomeViewModel
import com.unipi.george.chordshub.viewmodels.MainViewModel
import com.unipi.george.chordshub.viewmodels.user.UserViewModel
import kotlin.math.roundToInt
import com.unipi.george.chordshub.components.CardsView
import com.unipi.george.chordshub.components.LoadingView
import com.unipi.george.chordshub.repository.firestore.SongRepository
import kotlinx.coroutines.delay

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    mainViewModel: MainViewModel,
    userViewModel: UserViewModel,
    navController: NavController,
    onMenuClick: () -> Unit,
    profileImageUrl: String?
) {
    val selectedSongId by homeViewModel.selectedSongId.collectAsState()
    val songList by homeViewModel.songList.collectAsState()
    var selectedFilter by remember { mutableStateOf("All") }
    val selectedTitle = remember { mutableStateOf<String?>(null) }
    var artistMode by remember { mutableStateOf(false) }
    var artistList by remember { mutableStateOf<List<String>>(emptyList()) }
    val isMenuOpen by mainViewModel.isMenuOpen
    val isFullScreenState by homeViewModel.isFullScreen.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    var topBarOffset by rememberSaveable { mutableFloatStateOf(0f) }
    val profileImage by mainViewModel.profileImageUrl.collectAsState()
    //val genreFilters = listOf("All", "Pop", "Hip-Hop", "R&B", "Reggae")
    //val moodFilters = listOf("Happy", "Sad", "Chill", "Energetic")
    var showNoResults by remember { mutableStateOf(false) }

    // Nested Scroll για σωστό collapsing behavior
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val newOffset =
                    (topBarOffset + available.y).coerceIn(-30f, 0f) // TopBar moves up to -dp
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

    LaunchedEffect(Unit) {
        mainViewModel.setTopBarContent {
            FilterRow(
                selectedFilter = selectedFilter,
                onFilterChange = { selectedFilter = it }
            )
        }
    }
    val fetchArtists by homeViewModel.fetchArtists.collectAsState()
    val songRepo = remember { SongRepository(FirebaseFirestore.getInstance()) }

    LaunchedEffect(Unit) {
        if (fetchArtists != null) {
            songRepo.getAllArtists { fetched ->
                artistList = fetched
                artistMode = true
            }
            homeViewModel.resetFetchArtists()
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 40.dp)
            .nestedScroll(nestedScrollConnection)
            .background(Color.Transparent)
    ) {
        Column {
            // Top bar μόνο όταν δεν είμαστε στο song view
            if (selectedSongId == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset { IntOffset(0, topBarOffset.roundToInt()) }
                        .zIndex(1f)

                ) {

                }
            }

            // Main Content
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    selectedSongId == null && songList.isEmpty() && showNoResults -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.unfortunately_text),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }

                    selectedSongId == null && songList.isEmpty() -> LoadingView()

                    selectedSongId == null -> if (artistMode) {
                        val artistPairs = artistList.map { artist -> artist to "artist:$artist" }
                        CardsView(
                            songList = artistPairs,
                            homeViewModel = homeViewModel,
                            selectedTitle = selectedTitle,
                            columns = 3,
                            cardHeight = 100.dp,
                            fontSize = 12.sp,
                            onSongClick = { artistTag ->
                                val artist = artistTag.removePrefix("artist:")
                                homeViewModel.fetchFilteredSongs(artist)
                                selectedFilter = artist
                                artistMode = false
                            }
                        )
                    } else {
                        CardsView(songList, homeViewModel, selectedTitle)
                    }


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
}





