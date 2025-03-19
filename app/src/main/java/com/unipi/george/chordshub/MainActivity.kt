package com.unipi.george.chordshub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.unipi.george.chordshub.navigation.auth.AuthFlowNavGraph
import com.unipi.george.chordshub.navigation.main.MainScaffold
import com.unipi.george.chordshub.sharedpreferences.AppSettingsPreferences
import com.unipi.george.chordshub.ui.theme.ChordsHubTheme
import com.unipi.george.chordshub.utils.ObserveUserSession
import com.unipi.george.chordshub.viewmodels.MainViewModel
import com.unipi.george.chordshub.viewmodels.user.SessionViewModel
import com.unipi.george.chordshub.viewmodels.user.SettingsViewModel
import com.unipi.george.chordshub.viewmodels.SettingsViewModelFactory

class MainActivity : ComponentActivity() {
    private lateinit var sessionViewModel: SessionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appSettingsPreferences = AppSettingsPreferences(this)

        sessionViewModel = ViewModelProvider(this)[SessionViewModel::class.java]

        setContent {
            val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModelFactory(appSettingsPreferences))

            val darkModeState = settingsViewModel.darkMode.value
            val isUserLoggedInState = sessionViewModel.isUserLoggedInState

            ChordsHubTheme(darkTheme = darkModeState) {
                val navController = rememberNavController()

                ObserveUserSession(sessionViewModel)

                if (isUserLoggedInState.value) {
                    MainScaffold(navController)
                } else {
                    AuthFlowNavGraph(navController, isUserLoggedInState)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        sessionViewModel.endSession(isChangingConfigurations)
    }
}
