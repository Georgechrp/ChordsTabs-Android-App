package com.unipi.george.chordshub.screens

import android.widget.Toast
import com.unipi.george.chordshub.components.SongLine
import com.unipi.george.chordshub.repository.FirestoreRepository


fun getSongData(repository: FirestoreRepository, callback: (SongLine?, String?) -> Unit) {
    repository.getSongLyrics { lyrics ->
        if (lyrics.isNullOrEmpty()) {
            callback(null, null)
            return@getSongLyrics
        }

        repository.getSongChords { chords ->
            if (chords == null) {
                callback(null, null)
                return@getSongChords
            }

            repository.getSongArtist { artist ->
                val songLine = SongLine(lyrics.joinToString("\n"), chords)
                println("SongLine: $songLine, Artist: $artist")
                callback(songLine, artist)
            }
            println("Lyrics: ${lyrics.joinToString("\n")}")
            println("Chords: $chords")
        }

    }

}


