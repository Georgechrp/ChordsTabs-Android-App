package com.unipi.george.chordshub.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.unipi.george.chordshub.repository.FirestoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.google.firebase.firestore.FirebaseFirestore


class SearchViewModel : ViewModel() {
    private val repository = FirestoreRepository(FirebaseFirestore.getInstance())

    private val _searchResults = MutableStateFlow<List<Triple<String, String, String>>>(emptyList())
    val searchResults: StateFlow<List<Triple<String, String, String>>> = _searchResults


    private val _selectedSongId = MutableStateFlow<String?>(null)
    val selectedSongId: StateFlow<String?> = _selectedSongId

    fun searchSongs(query: String) {
        if (query.isEmpty()) {
            _searchResults.value = emptyList()
            return
        }

        repository.searchSongs(query) { results ->
            _searchResults.value = results
        }
    }


    fun selectSong(songId: String) {
        Log.d("SearchViewModel", "Setting selectedSongId to: $songId")
        _selectedSongId.value = songId
    }


    fun clearSelectedSong() {
        Log.d("SearchViewModel", "Clearing selectedSongId")
        _selectedSongId.value = null
    }

}
