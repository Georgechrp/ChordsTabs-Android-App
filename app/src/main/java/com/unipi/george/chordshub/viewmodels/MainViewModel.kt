package com.unipi.george.chordshub.viewmodels

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State

class MainViewModel : ViewModel() {
    private val _isFullScreen = mutableStateOf(false)
    val isFullScreen: State<Boolean> = _isFullScreen

    fun setFullScreen(value: Boolean) {
        _isFullScreen.value = value
    }
}
