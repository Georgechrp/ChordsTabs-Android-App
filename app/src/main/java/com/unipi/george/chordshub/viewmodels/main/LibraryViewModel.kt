package com.unipi.george.chordshub.viewmodels.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.unipi.george.chordshub.repository.FirestoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LibraryViewModel : ViewModel() {
    private val repository = FirestoreRepository(FirebaseFirestore.getInstance())
    private val auth = FirebaseAuth.getInstance()
    private val userId = auth.currentUser?.uid

    // Οι playlists του χρήστη
    private val _playlists = MutableStateFlow<List<String>>(emptyList())
    val playlists: StateFlow<List<String>> = _playlists

    init {
        loadLibraryData()
    }

    private fun loadLibraryData() {
        userId?.let {
            viewModelScope.launch {
                repository.getUserPlaylists(it) { userPlaylists ->
                    _playlists.value = userPlaylists
                }
            }
        }
    }

    fun createPlaylist(playlistName: String, onComplete: (Boolean) -> Unit) {
        userId?.let { id ->
            repository.createPlaylist(id, playlistName) { success ->
                if (success) {
                    loadLibraryData() // Επαναφόρτωση για να εμφανιστεί η νέα playlist
                }
                onComplete(success)
            }
        }
    }
}
