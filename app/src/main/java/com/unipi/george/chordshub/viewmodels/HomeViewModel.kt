package com.unipi.george.chordshub.viewmodels

import androidx.lifecycle.ViewModel
import com.unipi.george.chordshub.components.SongLine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel : ViewModel() {
    private val _selectedSong = MutableStateFlow<SongLine?>(null)
    val selectedSong: StateFlow<SongLine?> = _selectedSong

    private val _selectedArtist = MutableStateFlow<String?>(null)
    val selectedArtist: StateFlow<String?> = _selectedArtist

    fun setSelectedSong(song: SongLine?, artist: String?) {
        _selectedSong.value = song
        _selectedArtist.value = artist
    }

    fun clearSelectedSong() {
        _selectedSong.value = null
        _selectedArtist.value = null
    }
}
