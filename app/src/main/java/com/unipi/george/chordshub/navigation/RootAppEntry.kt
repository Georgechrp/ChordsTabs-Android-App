package com.unipi.george.chordshub.navigation


import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.unipi.george.chordshub.navigation.auth.AuthFlowNavGraph
import com.unipi.george.chordshub.navigation.main.MainScaffold
import com.unipi.george.chordshub.sharedpreferences.AppSettingsPreferences
import com.unipi.george.chordshub.ui.theme.ChordsHubTheme
import com.unipi.george.chordshub.utils.ObserveUserSession
import com.unipi.george.chordshub.viewmodels.SettingsViewModelFactory
import com.unipi.george.chordshub.viewmodels.user.SessionViewModel
import com.unipi.george.chordshub.viewmodels.user.SettingsViewModel

@Composable
fun RootAppEntry(
    sessionViewModel: SessionViewModel,
    appSettingsPreferences: AppSettingsPreferences
) {
    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(appSettingsPreferences)
    )

    val darkMode = settingsViewModel.darkMode.value
    val isUserLoggedInState = sessionViewModel.isUserLoggedInState

    ChordsHubTheme(darkTheme = darkMode) {
        val navController = rememberNavController()

        ObserveUserSession(sessionViewModel)

        if (isUserLoggedInState.value) {
            MainScaffold(
                navController = navController,
                sessionViewModel = sessionViewModel,
                mainViewModel = viewModel(),
                homeViewModel = viewModel()
            )

        } else {
            AuthFlowNavGraph(navController, isUserLoggedInState)
        }
    }
}
