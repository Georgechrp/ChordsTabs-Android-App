package com.unipi.george.chordshub.models

data class GuitarChord(
    val name: String,
    val fingerPositions: List<Int> // 0 για ανοιχτές, -1 για muted, αριθμοί για fret positions
)
