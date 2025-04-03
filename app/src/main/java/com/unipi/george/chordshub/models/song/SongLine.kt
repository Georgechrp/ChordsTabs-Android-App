package com.unipi.george.chordshub.models.song

data class SongLine(
    val lineNumber: Int,
    val text: String,
    val chords: List<ChordPosition> = emptyList()
)

data class ChordPosition(
    val chord: String,
    val position: Int
)
