package com.unipi.george.chordshub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import com.unipi.george.chordshub.navigation.main.RootAppEntry
import com.unipi.george.chordshub.sharedpreferences.AppSettingsPreferences
import com.unipi.george.chordshub.viewmodels.user.SessionViewModel


class MainActivity : ComponentActivity() {

    private lateinit var sessionViewModel: SessionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.TRANSPARENT

        AppContainer.appSettingsPreferences = AppSettingsPreferences(this)
        sessionViewModel = ViewModelProvider(this)[SessionViewModel::class.java]

        setContent {
            RootAppEntry(sessionViewModel)
        }
    }

    override fun onStop() {
        super.onStop()
        sessionViewModel.endSession(isChangingConfigurations)
    }
}

