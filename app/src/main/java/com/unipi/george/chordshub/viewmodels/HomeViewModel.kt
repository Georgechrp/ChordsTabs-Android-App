package com.unipi.george.chordshub.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.unipi.george.chordshub.models.SongLine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel : ViewModel() {
    private val _selectedSongId = MutableStateFlow<String?>(null) // ✅ Αποθηκεύουμε το ID του τραγουδιού
    val selectedSongId: StateFlow<String?> get() = _selectedSongId

    private val _selectedSong = MutableStateFlow<List<SongLine>?>(null) // ✅ Στίχοι τραγουδιού
    val selectedSong: StateFlow<List<SongLine>?> get() = _selectedSong

    private val _selectedArtist = MutableStateFlow<String?>(null)
    val selectedArtist: StateFlow<String?> get() = _selectedArtist

    fun selectSong(songId: String) {
        Log.d("HomeViewModel", "Setting selectedSongId: $songId") // ✅ DEBUGGING
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
    }
}

