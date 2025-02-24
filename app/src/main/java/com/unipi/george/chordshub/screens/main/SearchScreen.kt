package com.unipi.george.chordshub.screens.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unipi.george.chordshub.screens.seconds.DetailedSongView
import com.unipi.george.chordshub.utils.QRCodeScannerButton
import com.unipi.george.chordshub.viewmodels.SearchViewModel


import com.unipi.george.chordshub.components.AppTopBar

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = viewModel(),
    painter: Painter,
    onMenuClick: () -> Unit
) {
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    val searchResults by viewModel.searchResults.collectAsState()
    val selectedSongId by viewModel.selectedSongId.collectAsState()
    var isFullScreen by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AppTopBar(
                painter = painter,
                onMenuClick = onMenuClick
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Αναζήτηση",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    QRCodeScannerButton(viewModel)
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (selectedSongId == null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(if (isFullScreen) 0.dp else 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = searchText,
                            onValueChange = {
                                searchText = it
                                viewModel.searchSongs(it.text)
                            },
                            label = { Text("Αναζήτηση τραγουδιών") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn {
                        items(searchResults) { song ->
                            ListItem(
                                modifier = Modifier.clickable {
                                    viewModel.selectSong(song.second)
                                },
                                headlineContent = { Text(song.first) },
                                supportingContent = { Text("Καλλιτέχνης: ${song.second}\n🔍 Αντιστοίχιση: ${song.third}") }
                            )
                            //Divider()
                        }
                    }
                }
            } else {
                DetailedSongView(
                    songId = selectedSongId!!,
                    isFullScreen = isFullScreen,
                    onFullScreenChange = { isFullScreen = !isFullScreen },
                    onBack = {
                        isFullScreen = false
                        viewModel.clearSelectedSong()
                    }
                )
            }
        }
    }
}
