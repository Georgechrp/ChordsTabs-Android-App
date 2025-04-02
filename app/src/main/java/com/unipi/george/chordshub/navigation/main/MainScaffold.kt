package com.unipi.george.chordshub.navigation.main

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.unipi.george.chordshub.viewmodels.main.HomeViewModel
import com.unipi.george.chordshub.viewmodels.MainViewModel
import com.unipi.george.chordshub.viewmodels.user.SessionViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@Composable
fun MainScaffold(
    navController: NavHostController,
    sessionViewModel: SessionViewModel,
    mainViewModel: MainViewModel = viewModel(),
    homeViewModel: HomeViewModel = viewModel()
) {
    val isMenuOpen by mainViewModel.isMenuOpen
    val isFullScreen by homeViewModel.isFullScreen.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    //val bottomBarExcludedScreens = remember { setOf("DetailedSongView") }

    val bottomBarExcludedScreens = setOf("detailedSongView/{songTitle}")


    // Χειρισμός back για το μενού
    BackHandler(enabled = isMenuOpen) {
        Log.d("BackHandler", "Back button pressed - Closing Menu")
        mainViewModel.setMenuOpen(false)
    }

    Scaffold(
        containerColor = Color.Transparent // Κανένα φόντο
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Κύριο περιεχόμενο
            MainNavGraph(
                navController = navController,
                mainViewModel = mainViewModel,
                sessionViewModel = sessionViewModel
            )

            // 👇 BottomNavBar κάτω δεξιά, με zIndex
            val currentRoute = navBackStackEntry?.destination?.route
            if (!isFullScreen && currentRoute !in bottomBarExcludedScreens) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .align(Alignment.BottomCenter) // 👈 Το βάζει στο κάτω μέρος
                        .zIndex(1f) // Να πετάει πάνω από το υπόλοιπο
                ) {
                    MainBottomNavBar(navController = navController, isFullScreen = isFullScreen)
                }
            }
        }

    }

}
