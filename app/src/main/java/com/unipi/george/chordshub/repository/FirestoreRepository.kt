package com.unipi.george.chordshub.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.unipi.george.chordshub.models.ChordPosition
import com.unipi.george.chordshub.models.SongData
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.Query
import com.unipi.george.chordshub.models.SongLine

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
            val bpm = document.getLong("bpm")?.toInt()
            val genres = document.get("genres") as? List<String>
            val createdAt = document.getString("createdAt")
            val creatorId = document.getString("creatorId")

            // 🔹 Μετατροπή lyrics σε List<SongLine>
            val lyricsList = document.get("lyrics") as? List<Map<String, Any>>
            val lyrics = lyricsList?.map { item ->
                SongLine(
                    lineNumber = (item["lineNumber"] as? Long)?.toInt() ?: 0,
                    text = item["text"] as? String ?: "",
                    chords = (item["chords"] as? List<Map<String, Any>>)?.mapNotNull { chord ->
                        val chordName = chord["chord"] as? String
                        val position = (chord["position"] as? Long)?.toInt()
                        if (chordName != null && position != null) ChordPosition(chordName, position) else null
                    } ?: emptyList()
                )
            } ?: emptyList()

            // ✅ Δημιουργία SongData με τη σωστή δομή
            SongData(
                title = title,
                artist = artist,
                key = key,
                bpm = bpm,
                genres = genres,
                createdAt = createdAt,
                creatorId = creatorId,
                lyrics = lyrics // Τώρα είναι List<SongLine>
            )
        } catch (e: Exception) {
            println("❌ Firestore Error: ${e.message}")
            null
        }
    }



    suspend fun addSongData(songId: String, songData: SongData) {
        val songMap = hashMapOf(
            "title" to songData.title,
            "artist" to songData.artist,
            "key" to songData.key,
            "bpm" to songData.bpm,
            "genres" to songData.genres,
            "createdAt" to songData.createdAt,
            "creatorId" to songData.creatorId,
            "lyrics" to songData.lyrics?.map { line ->
                mapOf(
                    "lineNumber" to line.lineNumber,
                    "text" to line.text,
                    "chords" to line.chords.map { chord ->
                        mapOf("chord" to chord.chord, "position" to chord.position)
                    }
                )
            }
        )

        try {
            db.collection("songs").document(songId).set(songMap).await()
            Log.d("Firestore", "✅ Song added successfully: $songId")
        } catch (e: Exception) {
            Log.e("Firestore", "❌ Error adding song", e)
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


    fun searchSongs(query: String, callback: (List<Pair<String, String>>) -> Unit) {
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

                    // Μετατροπή σε πεζά για case-insensitive αναζήτηση
                    if (title.lowercase().contains(queryLower) || artist.lowercase().contains(queryLower)) {
                        title to artist
                    } else {
                        null
                    }
                }

                callback(results)
            }
            .addOnFailureListener { exception ->
                println("Error fetching search results: ${exception.message}")
                callback(emptyList())
            }
    }




}
