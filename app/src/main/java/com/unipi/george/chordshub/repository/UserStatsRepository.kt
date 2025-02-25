package com.unipi.george.chordshub.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class UserStatsRepository(private val db: FirebaseFirestore) {

    private val usersCollection = db.collection("users")

    /**
     * 🔥 Αυξάνει έναν αριθμητικό μετρητή στο Firestore για τον χρήστη
     */
    private fun incrementUserStat(userId: String, field: String) {
        val userRef = usersCollection.document(userId)
        db.runTransaction { transaction ->
            val snapshot = transaction.get(userRef)
            val currentValue = snapshot.getLong(field) ?: 0
            transaction.update(userRef, field, currentValue + 1)
        }.addOnSuccessListener {
            Log.d("Firestore", "✅ $field updated successfully for user $userId")
        }.addOnFailureListener { e ->
            Log.e("Firestore", "❌ Error updating $field", e)
        }
    }

    /**
     * 🔥 Αυξάνει το σύνολο των προβολών τραγουδιών του χρήστη
     */
    fun incrementTotalSongsViewed(userId: String) {
        incrementUserStat(userId, "totalSongsViewed")
    }

    /**
     * 🔥 Αυξάνει το σύνολο των τραγουδιών που ανέβασε ο χρήστης
     */
    fun incrementTotalSongsUploaded(userId: String) {
        incrementUserStat(userId, "totalSongsUploaded")
    }

    /**
     * 🔥 Αυξάνει το σύνολο των τραγουδιών που πρόσθεσε στα αγαπημένα
     */
    fun incrementTotalSongsFavorited(userId: String) {
        incrementUserStat(userId, "totalSongsFavorited")
    }

    /**
     * 🔥 Ενημερώνει το τελευταίο login του χρήστη
     */
    fun updateLastLogin(userId: String) {
        usersCollection.document(userId)
            .update("lastLogin", System.currentTimeMillis().toString())
            .addOnSuccessListener {
                Log.d("Firestore", "✅ Last login updated for user $userId")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "❌ Error updating last login", e)
            }
    }


    //Ενημερώνει το συνολικό χρόνο χρήσης της εφαρμογής (π.χ. σε λεπτά)

    fun updateTotalTimeSpent(userId: String, minutes: Int) {
        addTotalTimeSpentIfMissing(userId)

        val userRef = usersCollection.document(userId)
        db.runTransaction { transaction ->
            val snapshot = transaction.get(userRef)
            val currentValue = snapshot.getLong("totalTimeSpent") ?: 0
            transaction.update(userRef, "totalTimeSpent", currentValue + minutes)
        }.addOnSuccessListener {
            Log.d("Firestore", "✅ Total time spent updated successfully for user $userId")
        }.addOnFailureListener { e ->
            Log.e("Firestore", "❌ Error updating total time spent", e)
        }
    }


    // Προσθέτει το `totalTimeSpent` αν δεν υπάρχει

    fun addTotalTimeSpentIfMissing(userId: String) {
        val userRef = usersCollection.document(userId)

        userRef.get()
            .addOnSuccessListener { document ->
                if (document.exists() && !document.contains("totalTimeSpent")) {
                    userRef.update("totalTimeSpent", 0L) // ✅ Προσθήκη μόνο αν λείπει
                        .addOnSuccessListener {
                            Log.d("Firestore", "✅ Added totalTimeSpent = 0 for user $userId")
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firestore", "❌ Error adding totalTimeSpent", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "❌ Error fetching user document", e)
            }
    }
}
