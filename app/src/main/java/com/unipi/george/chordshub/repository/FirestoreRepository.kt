package com.unipi.george.chordshub.repository

import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.unipi.george.chordshub.components.ChordPosition

class FirestoreRepository(private val firestore: FirebaseFirestore) {

    //private val songDocument = firestore.collection("songs").document("a1zklgwjBnfms9vrPuln")

    private val db = FirebaseFirestore.getInstance()
    private var songDocument: DocumentReference? = null

    // Ενημέρωση του songDocument με το επιλεγμένο ID τραγουδιού
    fun setSongId(songId: String) {
        songDocument = firestore.collection("songs").document(songId)
    }

    fun getSongTitles(callback: (List<Pair<String, String>>) -> Unit) {
        db.collection("songs")
            .get()
            .addOnSuccessListener { result ->
                val titlesAndIds = mutableListOf<Pair<String, String>>()
                for (document in result) {
                    val title = document.getString("title")
                    val id = document.id // Αποκτάς το ID του τραγουδιού
                    if (title != null && id != null) {
                        titlesAndIds.add(Pair(title, id))
                    }
                }
                callback(titlesAndIds)
            }
            .addOnFailureListener { exception ->
                println("Error getting documents: $exception")
                callback(emptyList())
            }
    }


    fun getSongTitle(callback: (String?) -> Unit) {
        songDocument?.get()
            ?.addOnSuccessListener { document ->
                val title = document.getString("title")
                Log.d("Firestore", "Title: $title")
                callback(title)
            }
            ?.addOnFailureListener { e ->
                Log.e("Firestore", "Error retrieving title", e)
                callback(null)
            }
    }

    fun getSongKey(callback: (String?) -> Unit) {
        songDocument?.get()
            ?.addOnSuccessListener { document ->
                val key = document.getString("key")
                Log.d("Firestore", "Key: $key")
                callback(key)
            }
            ?.addOnFailureListener { e ->
                Log.e("Firestore", "Error retrieving key", e)
                callback(null)
            }
    }

    fun getSongArtist(callback: (String?) -> Unit) {
        songDocument?.get()
            ?.addOnSuccessListener { document ->
                val artist = document.getString("artist")
                Log.d("Firestore", "Artist: $artist")
                callback(artist)
            }
            ?.addOnFailureListener { e ->
                Log.e("Firestore", "Error retrieving artist", e)
                callback(null)
            }
    }

    fun getSongLyrics(callback: (List<String>?) -> Unit) {
        songDocument?.get()
            ?.addOnSuccessListener { document ->
                val lyricsList = document.get("lyrics") as? List<String>
                Log.d("Firestore", "Lyrics: $lyricsList")
                callback(lyricsList)
            }
            ?.addOnFailureListener { e ->
                Log.e("Firestore", "Error retrieving lyrics", e)
                callback(null)
            }
    }


    fun getSongChords(callback: (List<ChordPosition>?) -> Unit) {
        songDocument?.get()
            ?.addOnSuccessListener { document ->
                val chordsList = document.get("chords") as? List<Map<String, Any>>

                val chords = chordsList?.mapNotNull { item ->
                    try {
                        val chord = item["chord"] as? String
                        val position = when (val pos = item["position"]) {
                            is Long -> pos.toInt()  // Αν είναι Long, μετατροπή σε Int
                            is String -> pos.toIntOrNull()  // Αν είναι String, μετατροπή σε Int
                            else -> null
                        }

                        if (chord != null && position != null) {
                            ChordPosition(chord, position)
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        Log.e("Firestore", "Error parsing chord: $item", e)
                        null
                    }
                } ?: emptyList()

                Log.d("Firestore", "Parsed Chords: $chords")
                callback(chords)
            }
            ?.addOnFailureListener { e ->
                Log.e("Firestore", "Error retrieving chords", e)
                callback(null)
            }
    }

}
