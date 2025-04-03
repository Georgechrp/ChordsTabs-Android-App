package com.unipi.george.chordshub.models.chord

data class Chord(
    val chordName: String,
    val positions: String,
    val fingers: String
)

data class GuitarChord(
    val name: String,
    val fingerPositions: List<Int> // 0 για ανοιχτές, -1 για muted, αριθμοί για fret positions
)
