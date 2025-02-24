package com.unipi.george.chordshub.models

data class ChordPosition(
    val chord: String,
    val position: Int
)

data class SongLine(
    val lineNumber: Int,
    val text: String,
    val chords: List<ChordPosition> = emptyList()
)

data class Chord(
    val chordName: String,
    val positions: String,
    val fingers: String
)