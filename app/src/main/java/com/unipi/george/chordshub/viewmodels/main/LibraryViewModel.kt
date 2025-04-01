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

    private val _playlists = MutableStateFlow<Map<String, List<String>>>(emptyMap())
    val playlists: StateFlow<Map<String, List<String>>> = _playlists

    init {
        loadLibraryData()
    }

    private fun loadLibraryData() {
        userId?.let {
            viewModelScope.launch {
                repository.getUserPlaylistsWithSongs(it) { userPlaylists ->
                    _playlists.value = userPlaylists
                }
            }
        }
    }

    fun renamePlaylist(oldName: String, newName: String, onComplete: (Boolean) -> Unit) {
        userId?.let { id ->
            repository.renamePlaylist(id, oldName, newName) { success ->
                if (success) loadLibraryData()
                onComplete(success)
            }
        }
    }


    fun createPlaylist(playlistName: String, onComplete: (Boolean) -> Unit) {
        userId?.let { id ->
            repository.createPlaylist(id, playlistName) { success ->
                if (success) {
                    loadLibraryData()
                }
                onComplete(success)
            }
        }
    }

    fun addSongToPlaylist(playlistName: String, songTitle: String, onComplete: (Boolean) -> Unit) {
        userId?.let { id ->
            repository.addSongToPlaylist(id, playlistName, songTitle) { success ->
                if (success) {
                    loadLibraryData()
                }
                onComplete(success)
            }
        }
    }

    fun removeSongFromPlaylist(playlistName: String, songTitle: String, onComplete: (Boolean) -> Unit) {
        userId?.let { id ->
            repository.removeSongFromPlaylist(id, playlistName, songTitle) { success ->
                if (success) {
                    loadLibraryData()
                }
                onComplete(success)
            }
        }
    }

    fun deletePlaylist(playlistName: String, onComplete: (Boolean) -> Unit) {
        userId?.let { id ->
            repository.deletePlaylist(id, playlistName) { success ->
                if (success) {
                    loadLibraryData()
                }
                onComplete(success)
            }
        }
    }
}
