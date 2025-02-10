package com.unipi.george.chordshub.components

data class ChordPosition(
    val chord: String,
    val position: Int
)

data class SongLine(
    val lyrics: String,
    val chords: List<ChordPosition>
)

data class Chord(
    val chordName: String,
    val positions: String,
    val fingers: String
)