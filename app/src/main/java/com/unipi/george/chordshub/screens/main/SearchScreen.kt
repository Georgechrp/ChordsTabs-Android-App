package com.unipi.george.chordshub.screens.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.unipi.george.chordshub.viewmodels.SearchViewModel


@Composable
fun SearchScreen(
    navController: NavHostController,
    viewModel: SearchViewModel = viewModel() // Προαιρετικό, θα δημιουργηθεί αν δεν δοθεί
) {
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    val searchResults by viewModel.searchResults.collectAsState()
    val selectedSongId by viewModel.selectedSongId.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (selectedSongId == null) {
            TextField(
                value = searchText,
                onValueChange = {
                    searchText = it
                    viewModel.searchSongs(it.text)
                },
                label = { Text("Αναζήτηση τραγουδιών") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(searchResults) { song ->
                    ListItem(
                        modifier = Modifier.clickable {
                            viewModel.selectSong(song.second)
                        },
                        headlineContent = { Text(song.first) },
                        supportingContent = { Text("Καλλιτέχνης: ${song.second}") }
                    )
                    Divider()
                }
            }
        } else {
            DetailedSongView(
                isFullScreen = true,
                onFullScreenChange = { viewModel.clearSelectedSong() },
                onBack = { viewModel.clearSelectedSong() }
            )
        }
    }
}
