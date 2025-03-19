package com.unipi.george.chordshub.viewmodels

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {
    private val _isMenuOpen = mutableStateOf(false)
    val isMenuOpen: State<Boolean> = _isMenuOpen

    private val _profileImageUrl = MutableStateFlow<String?>(null)
    val profileImageUrl = _profileImageUrl.asStateFlow()

    fun setProfileImageUrl(url: String?) {
        _profileImageUrl.value = url
    }

    fun setMenuOpen(value: Boolean) {
        _isMenuOpen.value = value
    }
}

