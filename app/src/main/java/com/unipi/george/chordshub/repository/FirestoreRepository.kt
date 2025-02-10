package com.unipi.george.chordshub.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.unipi.george.chordshub.components.ChordPosition
import com.unipi.george.chordshub.models.SongData
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.Query

class FirestoreRepository(private val firestore: FirebaseFirestore) {

    private val db = FirebaseFirestore.getInstance()
    private var songDocument: DocumentReference? = null
    private val _songs = MutableLiveData<List<SongData>>()
    val songs: LiveData<List<SongData>> = _songs

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
                    val id = document.id
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

    suspend fun getSongDataAsync(): SongData? {
        return try {
            val document = songDocument?.get()?.await() ?: return null
            val title = document.getString("title")
            val artist = document.getString("artist")
            val key = document.getString("key")
            val lyrics = document.get("lyrics") as? List<String>
            val genres = document.get("genres") as? List<String>

            println("Lyrics: $lyrics")
            println("Artist: $artist")
            println("Key: $key")
            println("Title: $title")
            println("Document: $document")
            println("Document ID: ${document.id}")
            println("Document Data: ${document.data}")
            println("Genres: $genres")

            val chordsList = document.get("chords") as? List<Map<String, Any>>
            val chords = chordsList?.mapNotNull { item ->
                val chord = item["chord"] as? String
                val position = when (val pos = item["position"]) {
                    is Long -> pos.toInt()
                    is String -> pos.toIntOrNull()
                    else -> null
                }
                if (chord != null && position != null) {
                    ChordPosition(chord, position)
                } else {
                    null
                }
            }
            println("Parsed Chords: $chords")

            SongData(title, artist, key, lyrics, chords, genres)
        } catch (e: Exception) {
            Log.e("Firestore", "Error retrieving song data", e)
            null
        }
    }


    //Για προσωρινη χρήση
    suspend fun addSongData(songId: String, songData: SongData) {
        val songMap = hashMapOf(
            "title" to songData.title,
            "artist" to songData.artist,
            "key" to songData.key,
            "lyrics" to songData.lyrics,
            "chords" to songData.chords?.map {
                mapOf("chord" to it.chord, "position" to it.position)
            },
            "genres" to songData.genres
        )

        try {
            db.collection("songs").document(songId).set(songMap).await()
            Log.d("Firestore", "Song added successfully: $songId")
        } catch (e: Exception) {
            Log.e("Firestore", "Error adding song", e)
        }
    }

    fun getFilteredSongs(filter: String, callback: (List<Pair<String, String>>) -> Unit) {
        println("🔍 Querying Firestore with filter: $filter")

        // Declare as Query instead of CollectionReference
        var query: Query = db.collection("songs")

        if (filter != "All") {
            // Reassign the variable with the filtered Query
            query = query.whereArrayContains("genres", filter)
        }

        query.get()
            .addOnSuccessListener { result ->
                val songList = result.documents.mapNotNull { doc ->
                    val title = doc.getString("title")
                    val id = doc.id
                    val genres = doc.get("genres") as? List<String>
                    println("Genres for song $title: $genres")
                    if (title != null) title to id else null
                }
                println("🔥 Firestore returned ${songList.size} results for filter: $filter")
                callback(songList)
            }
            .addOnFailureListener { exception ->
                println("❌ Firestore error: ${exception.message}")
            }
    }

}
