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

    private lateinit var sessionViewModel: SessionViewModel  //late initializing sessionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)// UI starts from (0,0)
        window.statusBarColor = android.graphics.Color.BLACK

        AppContainer.appSettingsPreferences = AppSettingsPreferences(this) //load app settings else works with default values
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

