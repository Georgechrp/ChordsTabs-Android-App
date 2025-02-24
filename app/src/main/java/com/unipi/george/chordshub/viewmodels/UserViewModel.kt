package com.unipi.george.chordshub.viewmodels

import com.unipi.george.chordshub.repository.AuthRepository
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel

class UserViewModel : ViewModel() {
    private val _userId = mutableStateOf<String?>(null)
    val userId: State<String?> = _userId

    init {
        loadUserId()
    }

    private fun loadUserId() {
        _userId.value = AuthRepository.getUserId()
    }

    fun updateUserId(newUserId: String?) {
        _userId.value = newUserId
    }
}
