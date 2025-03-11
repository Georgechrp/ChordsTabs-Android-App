package com.unipi.george.chordshub.viewmodels

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State

class MainViewModel : ViewModel() {


    private val _isMenuOpen = mutableStateOf(false)
    val isMenuOpen: State<Boolean> = _isMenuOpen


    fun setMenuOpen(value: Boolean) {
        _isMenuOpen.value = value
    }
}

