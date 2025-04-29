package com.unipi.george.chordshub.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.unipi.george.chordshub.repository.firestore.TempPlaylistRepository
import com.unipi.george.chordshub.viewmodels.seconds.TempPlaylistViewModel

class TempPlaylistViewModelFactory(

    private val repository: TempPlaylistRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TempPlaylistViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TempPlaylistViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
