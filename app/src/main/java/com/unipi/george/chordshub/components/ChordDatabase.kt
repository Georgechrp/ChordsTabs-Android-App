package com.unipi.george.chordshub.components


fun fetchChordDetails(chordName: String): Chord? {
    val chordDatabase = mapOf(
        "Em" to Chord("Em", "0 2 2 0 0 0", "X 2 3 1 1 1"),
        "Am" to Chord("Am", "X 0 2 2 1 0", "X 1 3 2 1 1"),
        "C" to Chord("C", "X 3 2 0 1 0", "X 3 2 0 1 0"),
        "G" to Chord("G", "3 2 0 0 0 3", "2 1 0 0 0 3"),
        "D" to Chord("D", "X X 0 2 3 2", "0 0 0 1 3 2")
    )
    return chordDatabase[chordName]
}