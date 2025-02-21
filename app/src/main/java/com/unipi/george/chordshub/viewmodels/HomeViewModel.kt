package com.unipi.george.chordshub.viewmodels

import androidx.lifecycle.ViewModel
import com.unipi.george.chordshub.models.SongLine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel : ViewModel() {
    private val _selectedSong = MutableStateFlow<List<SongLine>?>(null) // ✅ Τώρα αποθηκεύει λίστα στίχων
    val selectedSong: StateFlow<List<SongLine>?> get() = _selectedSong

    private val _selectedArtist = MutableStateFlow<String?>(null)
    val selectedArtist: StateFlow<String?> get() = _selectedArtist

    fun setSelectedSong(song: List<SongLine>, artist: String?) {
        _selectedSong.value = song // ✅ Τώρα η ανάθεση είναι σωστή
        _selectedArtist.value = artist
    }

    fun clearSelectedSong() {
        _selectedSong.value = null
        _selectedArtist.value = null
    }
}
