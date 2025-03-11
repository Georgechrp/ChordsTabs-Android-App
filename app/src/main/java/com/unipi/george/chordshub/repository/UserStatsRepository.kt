package com.unipi.george.chordshub.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.text.SimpleDateFormat
import java.util.*

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


    fun incrementTotalSongsViewed(userId: String) {
        incrementUserStat(userId, "totalSongsViewed")
    }

    fun incrementTotalSongsUploaded(userId: String) {
        incrementUserStat(userId, "totalSongsUploaded")
    }


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

    private fun addTotalTimeSpentIfMissing(userId: String) {
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

    // Επιστρέφει τη Δευτέρα της τρέχουσας εβδομάδας
    private fun getCurrentWeekStartDate(): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
    }

    // Επιστρέφει την ημέρα της εβδομάδας (π.χ. "Monday")
    private fun getCurrentDayOfWeek(): String {
        return SimpleDateFormat("EEEE", Locale.getDefault()).format(Date())
    }

    fun updateWeeklyStats(userId: String, currentDay: String, timeSpent: Int) {
        val weekStart = getCurrentWeekStartDate()

        val userStatsRef = db.collection("users").document(userId)  // Ενημέρωση στο σωστό collection

        userStatsRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val data = document.data ?: return@addOnSuccessListener

                // Αν η εβδομάδα δεν είναι ίδια, δημιουργούμε νέο πεδίο
                if (data["weekStart"] != weekStart) {
                    val newWeekStats = mapOf(
                        "weekStart" to weekStart,
                        "totalTimeSpent" to mapOf(
                            "Monday" to "-",
                            "Tuesday" to "-",
                            "Wednesday" to "-",
                            "Thursday" to "-",
                            "Friday" to "-",
                            "Saturday" to "-",
                            "Sunday" to "-"
                        )
                    )
                    userStatsRef.set(newWeekStats, SetOptions.merge())
                }

                // Δημιουργία ή Ενημέρωση του σημερινού χρόνου
                val updatedDay = mapOf("totalTimeSpent.$currentDay" to timeSpent)
                userStatsRef.set(updatedDay, SetOptions.merge())
            } else {
                // Αν δεν υπάρχει καθόλου εγγραφή, δημιουργούμε νέα
                val initialStats = mapOf(
                    "weekStart" to weekStart,
                    "totalTimeSpent" to mapOf(
                        "Monday" to "-",
                        "Tuesday" to "-",
                        "Wednesday" to "-",
                        "Thursday" to "-",
                        "Friday" to "-",
                        "Saturday" to "-",
                        "Sunday" to timeSpent
                    )
                )
                userStatsRef.set(initialStats, SetOptions.merge())
            }
        }.addOnFailureListener { e ->
            println("❌ Σφάλμα κατά την ενημέρωση: ${e.message}")
        }
    }
}
