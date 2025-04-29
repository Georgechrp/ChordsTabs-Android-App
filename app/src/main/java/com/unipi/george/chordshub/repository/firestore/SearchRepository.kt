package com.unipi.george.chordshub.repository.firestore

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.unipi.george.chordshub.models.song.Song

class SearchRepository(private val db: FirebaseFirestore) {

    private val _songs = MutableLiveData<List<Song>>()
    val songs: LiveData<List<Song>> = _songs


    fun searchSongs(query: String, callback: (List<Triple<String, String, String>>) -> Unit) {
        if (query.isEmpty()) {
            callback(emptyList())
            return
        }

        db.collection("songs")
            .get()
            .addOnSuccessListener { result ->
                val queryLower = query.lowercase()

                val results = result.documents.mapNotNull { doc ->
                    val title = doc.getString("title") ?: ""
                    val artist = doc.getString("artist") ?: "Άγνωστος Καλλιτέχνης"
                    val docId = doc.id

                    val lyricsList = doc.get("lyrics") as? List<Map<String, Any>>
                    val lyricsMatch = lyricsList?.firstOrNull { line ->
                        val lineText = line["text"] as? String ?: ""
                        lineText.lowercase().contains(queryLower)
                    }

                    when {
                        title.lowercase().contains(queryLower) -> Triple(title, docId, "Τίτλος")
                        artist.lowercase().contains(queryLower) -> Triple(title, docId, "Καλλιτέχνης")
                        lyricsMatch != null -> Triple(title, docId, "Στίχος")
                        else -> null
                    }
                }

                callback(results)
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "❌ Error fetching search results: ${exception.message}")
                callback(emptyList())
            }
    }


}