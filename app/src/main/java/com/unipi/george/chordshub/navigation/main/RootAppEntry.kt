package com.unipi.george.chordshub.navigation.main


import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.unipi.george.chordshub.navigation.auth.AuthFlowNavGraph
import com.unipi.george.chordshub.sharedpreferences.AppSettingsPreferences
import com.unipi.george.chordshub.ui.theme.ChordsHubTheme
import com.unipi.george.chordshub.utils.ObserveUserSession
import com.unipi.george.chordshub.viewmodels.SettingsViewModelFactory
import com.unipi.george.chordshub.viewmodels.user.SessionViewModel
import com.unipi.george.chordshub.viewmodels.user.SettingsViewModel

/*
 *
 * - Applies the selected theme (light/dark) based on user preferences
 * - Observes the user's session status
 * - Decides whether to show the authenticated MainScaffold (main app) or the AuthFlowNavGraph (login/register)
 * - Instantiates SettingsViewModel with AppSettingsPreferences
 *
 */


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
