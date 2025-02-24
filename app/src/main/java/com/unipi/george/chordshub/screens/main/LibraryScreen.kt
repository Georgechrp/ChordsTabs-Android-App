package com.unipi.george.chordshub.screens.main


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.navigation.NavController
import com.unipi.george.chordshub.components.AppTopBar

@Composable
fun LibraryScreen(navController: NavController, painter: Painter, onMenuClick: () -> Unit) {
    Scaffold(
        topBar = {
            AppTopBar(
                painter = painter,
                onMenuClick = onMenuClick
            ) {
                Text("Βιβλιοθήκη", style = MaterialTheme.typography.headlineSmall)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Library Screen")
        }
    }
}
