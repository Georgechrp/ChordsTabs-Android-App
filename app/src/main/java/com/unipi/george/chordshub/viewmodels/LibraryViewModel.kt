package com.unipi.george.chordshub.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.unipi.george.chordshub.repository.FirestoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LibraryViewModel : ViewModel() {
    private val repository = FirestoreRepository(FirebaseFirestore.getInstance())

    // Τα τραγούδια που έχει αποθηκεύσει ο χρήστης
    private val _savedSongs = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val savedSongs: StateFlow<List<Pair<String, String>>> = _savedSongs

    // Οι playlists του χρήστη
    private val _playlists = MutableStateFlow<List<String>>(emptyList())
    val playlists: StateFlow<List<String>> = _playlists

    init {
        loadLibraryData()
    }

    private fun loadLibraryData() {
        /*viewModelScope.launch {
            repository.getSavedSongs { songs ->
                _savedSongs.value = songs.map { it.first to it.second }
            }
            repository.getUserPlaylists { userPlaylists ->
                _playlists.value = userPlaylists
            }
        }*/
    }
}
