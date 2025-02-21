package com.unipi.george.chordshub.models

data class SongData(
    val title: String?,
    val artist: String?,
    val key: String?,
    val bpm: Int?,
    val genres: List<String>? = null,
    val createdAt: String?,
    val creatorId: String?,
    val lyrics: List<SongLine>?,
)

