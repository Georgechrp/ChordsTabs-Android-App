package com.unipi.george.chordshub.models

data class ChordPosition(
    val chord: String,
    val position: Int
)

data class SongLine(
    val lineNumber: Int,  // Προσθήκη αριθμού γραμμής για sorting
    val text: String,      // Το περιεχόμενο του στίχου
    val chords: List<ChordPosition> = emptyList()  // Οι συγχορδίες σε αυτή τη γραμμή
)

data class Chord(
    val chordName: String,
    val positions: String,  // Οι θέσεις των δαχτύλων (π.χ. "x32010")
    val fingers: String     // Το πώς πρέπει να τοποθετηθούν τα δάχτυλα
)