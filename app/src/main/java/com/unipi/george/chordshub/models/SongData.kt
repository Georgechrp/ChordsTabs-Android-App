package com.unipi.george.chordshub.models

import com.unipi.george.chordshub.components.ChordPosition

data class SongData(
    val title: String?,
    val artist: String?,
    val key: String?,
    val lyrics: List<String>?,
    val chords: List<ChordPosition>?,
    val genres: List<String>? = null
)


