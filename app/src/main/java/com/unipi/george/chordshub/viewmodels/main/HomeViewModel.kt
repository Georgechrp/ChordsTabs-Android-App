package com.unipi.george.chordshub.viewmodels.main

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.unipi.george.chordshub.models.song.SongLine
import com.unipi.george.chordshub.repository.FirestoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel : ViewModel() {
    private val _selectedSongId = MutableStateFlow<String?>(null)
    val selectedSongId: StateFlow<String?> get() = _selectedSongId

    private val _selectedSong = MutableStateFlow<List<SongLine>?>(null)
    val selectedSong: StateFlow<List<SongLine>?> get() = _selectedSong

    private val _selectedArtist = MutableStateFlow<String?>(null)
    val selectedArtist: StateFlow<String?> get() = _selectedArtist

    private val _songList = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val songList: StateFlow<List<Pair<String, String>>> get() = _songList

    private val _isFullScreen = MutableStateFlow(false)
    val isFullScreen: StateFlow<Boolean> get() = _isFullScreen

    private val _showBottomBar = MutableStateFlow(true)
    val showBottomBar: StateFlow<Boolean> = _showBottomBar

    fun setShowBottomBar(visible: Boolean) {
        _showBottomBar.value = visible
    }


    fun setFullScreen(value: Boolean) {
        _isFullScreen.value = value
    }

    fun fetchFilteredSongs(filter: String) {
        val repository = FirestoreRepository(FirebaseFirestore.getInstance())
        repository.getFilteredSongs(filter) { titlesAndIds ->
            _songList.value = titlesAndIds
        }
    }
    fun selectSong(songId: String) {
        Log.d("HomeViewModel", "Setting selectedSongId: $songId")
        _selectedSongId.value = songId
    }

    fun setSelectedSong(song: List<SongLine>, artist: String?) {
        _selectedSong.value = song
        _selectedArtist.value = artist
    }

    fun clearSelectedSong() {
        Log.d("HomeViewModel", "Clearing selectedSongId and selectedSong")
        _selectedSongId.value = null
        _selectedSong.value = null
        _selectedArtist.value = null
        _isFullScreen.value = false
    }


}

