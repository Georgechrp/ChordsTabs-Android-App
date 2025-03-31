package com.unipi.george.chordshub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.unipi.george.chordshub.navigation.RootAppEntry
import com.unipi.george.chordshub.sharedpreferences.AppSettingsPreferences
import com.unipi.george.chordshub.viewmodels.user.SessionViewModel

class MainActivity : ComponentActivity() {

    private lateinit var sessionViewModel: SessionViewModel
    private lateinit var appSettingsPreferences: AppSettingsPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔧 Αρχικοποίηση των dependencies (ViewModel, Preferences)
        setupDependencies()

        // 🧩 Ορίζουμε το βασικό UI της εφαρμογής
        setContent {
            RootAppEntry(
                sessionViewModel = sessionViewModel,
                appSettingsPreferences = appSettingsPreferences
            )
        }
    }

    private fun setupDependencies() {
        appSettingsPreferences = AppSettingsPreferences(this)
        sessionViewModel = ViewModelProvider(this)[SessionViewModel::class.java]
    }

    override fun onStop() {
        super.onStop()
        sessionViewModel.endSession(isChangingConfigurations)
    }
}
