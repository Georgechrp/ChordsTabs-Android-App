package com.unipi.george.chordshub.navigation.main

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
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

/*
 * Here we have the Main UI
 * - Navigate to MainNavGraph((navigation to all Screens))
 * - Handle the manu state from back button
 * - Prints Bottom Nav Bar when we are in the mainScreens
 */

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScaffold(
    navController: NavHostController,
    sessionViewModel: SessionViewModel
) {
    val mainViewModel: MainViewModel = viewModel()
    val homeViewModel: HomeViewModel = viewModel()

    val isMenuOpen by mainViewModel.isMenuOpen
    val isFullScreen by homeViewModel.isFullScreen.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val bottomBarExcludedScreens = setOf("detailedSongView/{songTitle}")

    BackHandler(enabled = isMenuOpen) {
        Log.d("BackHandler", "Back button pressed - Closing Menu")
        mainViewModel.setMenuOpen(false)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { _ ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()

        ) {
            MainNavGraph(
                navController = navController,
                mainViewModel = mainViewModel,
                sessionViewModel = sessionViewModel
            )

            // BottomNavBar με zIndex
            val currentRoute = navBackStackEntry?.destination?.route
            val showBottomBar by homeViewModel.showBottomBar.collectAsState()

            if (!isFullScreen && currentRoute !in bottomBarExcludedScreens) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .align(Alignment.BottomCenter)
                        .zIndex(1f)
                ) {
                    if (showBottomBar && currentRoute !in bottomBarExcludedScreens) {
                        BottomNavBar(navController = navController, isFullScreen = isFullScreen)
                    }
                }
            }
        }
    }
}
