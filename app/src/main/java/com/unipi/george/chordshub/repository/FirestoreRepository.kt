package com.unipi.george.chordshub.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.unipi.george.chordshub.models.song.Song
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.Query
import com.unipi.george.chordshub.models.song.ChordPosition
import com.unipi.george.chordshub.models.song.SongLine

class FirestoreRepository(private val firestore: FirebaseFirestore) {

    private val db = FirebaseFirestore.getInstance()
    private var songDocument: DocumentReference? = null
    private val _songs = MutableLiveData<List<Song>>()
    val songs: LiveData<List<Song>> = _songs

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

    suspend fun getSongDataAsync(songId: String): Song? {
        Log.d("Firestore", "Fetching song data for ID: $songId")

        return try {
            val document = db.collection("songs").document(songId).get().await()
            if (!document.exists()) {
                Log.e("Firestore", "❌ No song found with ID: $songId")
                return null
            }

            val title = document.getString("title") ?: "Unknown Title"
            val artist = document.getString("artist") ?: "Unknown Artist"
            val key = document.getString("key") ?: "Unknown Key"
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

            Log.d("Firestore", "✅ Song loaded successfully: $title")
            return Song(
                title = title,
                artist = artist,
                key = key,
                bpm = bpm,
                genres = genres,
                createdAt = createdAt,
                creatorId = creatorId,
                lyrics = lyrics
            )
        } catch (e: Exception) {
            Log.e("Firestore", "❌ Firestore Error: ${e.message}")
            return null
        }
    }

    fun getSongsByArtist(artistName: String, callback: (List<Song>) -> Unit) {
        db.collection("songs")
            .whereEqualTo("artist", artistName)
            .get()
            .addOnSuccessListener { result ->
                val songs = result.documents.mapNotNull { it.toObject(Song::class.java) }
                callback(songs)
            }
            .addOnFailureListener { exception ->
                println("Error fetching songs for artist $artistName: $exception")
                callback(emptyList())
            }
    }

    suspend fun addSongData(songId: String, song: Song) {
        val songMap = hashMapOf(
            "title" to song.title,
            "artist" to song.artist,
            "key" to song.key,
            "bpm" to song.bpm,
            "genres" to song.genres,
            "createdAt" to song.createdAt,
            "creatorId" to song.creatorId,
            "lyrics" to song.lyrics?.map { line ->
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

    fun getRecentSongs(userId: String, callback: (List<Pair<String, String>>) -> Unit) {
        val userDocRef = db.collection("users").document(userId)

        userDocRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val recentSongs = document.get("recentSongs") as? List<String> ?: emptyList()

                    if (recentSongs.isEmpty()) {
                        println("🔍 No recent songs found for user: $userId")
                        callback(emptyList()) // Επιστρέφει κενή λίστα αν δεν υπάρχουν recent τραγούδια
                        return@addOnSuccessListener
                    }

                    println("🎵 Recent songs Titles: $recentSongs")

                    // Ανακτά τα τραγούδια που έχουν τίτλους από το `recentSongs`
                    db.collection("songs")
                        .whereIn("title", recentSongs)
                        .get()
                        .addOnSuccessListener { result ->
                            val songList = result.documents.mapNotNull { doc ->
                                val title = doc.getString("title")
                                val id = doc.id
                                if (title != null) title to id else null
                            }

                            println("🔥 Firestore returned ${songList.size} recent songs")
                            callback(songList)
                        }
                        .addOnFailureListener { exception ->
                            println("❌ Firestore error fetching songs: ${exception.message}")
                            callback(emptyList())
                        }
                } else {
                    println("❌ User document not found: $userId")
                    callback(emptyList())
                }
            }
            .addOnFailureListener { exception ->
                println("❌ Firestore error fetching user: ${exception.message}")
                callback(emptyList())
            }
    }


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


    fun getRandomSongs(limit: Int, callback: (List<Pair<String, String>>) -> Unit) {
        db.collection("songs")
            .limit(limit.toLong())
            .get()
            .addOnSuccessListener { result ->
                val songList = result.documents.mapNotNull { doc ->
                    val title = doc.getString("title")
                    val id = doc.id
                    if (title != null) title to id else null
                }
                callback(songList)
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "❌ Error fetching random songs: ${exception.message}")
                callback(emptyList())
            }
    }


    //Library functions
    fun createPlaylist(userId: String, playlistName: String, callback: (Boolean) -> Unit) {
        val userDocRef = db.collection("users").document(userId)

        val newPlaylist = hashMapOf(
            "name" to playlistName,
            "songs" to emptyList<String>() // Κενή λίστα τραγουδιών αρχικά
        )

        userDocRef.update("playlists", FieldValue.arrayUnion(newPlaylist))
            .addOnSuccessListener {
                Log.d("Firestore", "✅ Playlist '$playlistName' created successfully.")
                callback(true)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "❌ Error creating playlist: ${e.message}")
                callback(false)
            }
    }

    fun getUserPlaylists(userId: String, callback: (List<String>) -> Unit) {
        val userDocRef = db.collection("users").document(userId)

        userDocRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val playlists = document.get("playlists") as? List<Map<String, Any>>
                    val playlistNames = playlists?.map { it["name"] as? String ?: "Άγνωστη Playlist" } ?: emptyList()
                    callback(playlistNames)
                } else {
                    callback(emptyList())
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "❌ Error fetching playlists: ${e.message}")
                callback(emptyList())
            }
    }

    fun getUserPlaylistsWithSongs(userId: String, callback: (Map<String, List<String>>) -> Unit) {
        val userDocRef = db.collection("users").document(userId)

        userDocRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val playlists = document.get("playlists") as? List<Map<String, Any>>
                    val playlistMap = mutableMapOf<String, List<String>>()

                    playlists?.forEach { playlist ->
                        val playlistName = playlist["name"] as? String ?: "Άγνωστη Playlist"
                        val songs = playlist["songs"] as? List<String> ?: emptyList()
                        playlistMap[playlistName] = songs
                    }

                    callback(playlistMap)
                } else {
                    callback(emptyMap())
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "❌ Error fetching playlists with songs: ${e.message}")
                callback(emptyMap())
            }
    }


    fun addSongToPlaylist(userId: String, playlistName: String, songTitle: String, callback: (Boolean) -> Unit) {
        val userDocRef = db.collection("users").document(userId)

        userDocRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val playlists = document.get("playlists") as? MutableList<Map<String, Any>> ?: mutableListOf()

                    // Βρίσκουμε την αντίστοιχη playlist
                    val updatedPlaylists = playlists.map { playlist ->
                        if (playlist["name"] == playlistName) {
                            val updatedSongs = (playlist["songs"] as? MutableList<String>) ?: mutableListOf()
                            if (!updatedSongs.contains(songTitle)) {
                                updatedSongs.add(songTitle)
                            }
                            playlist.toMutableMap().apply { put("songs", updatedSongs) }
                        } else {
                            playlist
                        }
                    }

                    userDocRef.update("playlists", updatedPlaylists)
                        .addOnSuccessListener { callback(true) }
                        .addOnFailureListener { callback(false) }
                } else {
                    callback(false)
                }
            }
            .addOnFailureListener { callback(false) }
    }

    fun removeSongFromPlaylist(userId: String, playlistName: String, songTitle: String, callback: (Boolean) -> Unit) {
        val userDocRef = db.collection("users").document(userId)

        userDocRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val playlists = document.get("playlists") as? MutableList<Map<String, Any>> ?: mutableListOf()

                    // Βρίσκουμε την playlist και αφαιρούμε το τραγούδι
                    val updatedPlaylists = playlists.map { playlist ->
                        if (playlist["name"] == playlistName) {
                            val updatedSongs = (playlist["songs"] as? MutableList<String>) ?: mutableListOf()
                            updatedSongs.remove(songTitle)
                            playlist.toMutableMap().apply { put("songs", updatedSongs) }
                        } else {
                            playlist
                        }
                    }

                    userDocRef.update("playlists", updatedPlaylists)
                        .addOnSuccessListener { callback(true) }
                        .addOnFailureListener { callback(false) }
                } else {
                    callback(false)
                }
            }
            .addOnFailureListener { callback(false) }
    }

    fun deletePlaylist(userId: String, playlistName: String, callback: (Boolean) -> Unit) {
        val userDocRef = db.collection("users").document(userId)

        userDocRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val playlists = document.get("playlists") as? MutableList<Map<String, Any>> ?: mutableListOf()

                    // Διαγράφουμε την playlist από τη λίστα
                    val updatedPlaylists = playlists.filter { it["name"] != playlistName }

                    userDocRef.update("playlists", updatedPlaylists)
                        .addOnSuccessListener { callback(true) }
                        .addOnFailureListener { callback(false) }
                } else {
                    callback(false)
                }
            }
            .addOnFailureListener { callback(false) }
    }


    fun renamePlaylist(userId: String, oldName: String, newName: String, callback: (Boolean) -> Unit) {
        val userDocRef = db.collection("users").document(userId)

        userDocRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val playlists = document.get("playlists") as? MutableList<Map<String, Any>> ?: mutableListOf()
                    val updatedPlaylists = playlists.map {
                        if (it["name"] == oldName) {
                            it.toMutableMap().apply { put("name", newName) }
                        } else it
                    }
                    userDocRef.update("playlists", updatedPlaylists)
                        .addOnSuccessListener { callback(true) }
                        .addOnFailureListener { callback(false) }
                } else callback(false)
            }
            .addOnFailureListener { callback(false) }
    }



}
