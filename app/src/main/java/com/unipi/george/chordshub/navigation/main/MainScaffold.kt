package com.unipi.george.chordshub.navigation.main

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.unipi.george.chordshub.viewmodels.main.HomeViewModel
import com.unipi.george.chordshub.viewmodels.MainViewModel


@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun MainScaffold(navController: NavHostController) {
    val mainViewModel: MainViewModel = viewModel()
    val homeViewModel: HomeViewModel = viewModel()
    val isMenuOpen by mainViewModel.isMenuOpen
    val bottomBarExcludedScreens = setOf("DetailedSongView")

    BackHandler(enabled = isMenuOpen) {
        Log.d("BackHandler", "Back button pressed - Closing Menu")
        mainViewModel.setMenuOpen(false)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                containerColor = Color.Transparent,
                bottomBar = {
                    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                    if (!homeViewModel.isFullScreen.value && currentRoute !in bottomBarExcludedScreens) {
                        MainBottomNavBar(navController, homeViewModel.isFullScreen.value)
                    }
                }
            ) { innerPadding ->
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                    MainNavGraph(navController, mainViewModel)
                }
            }
        }

    }
}