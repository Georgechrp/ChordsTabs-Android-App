package com.unipi.george.chordshub.repository

import android.annotation.SuppressLint
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.runtime.mutableStateOf


object AuthRepository {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    @SuppressLint("StaticFieldLeak")
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    val isUserLoggedInState = mutableStateOf(isUserLoggedIn())
    val fullNameState = mutableStateOf(getFullName())

    fun signInUser(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    isUserLoggedInState.value = true
                    onResult(true, null)
                } else {
                    onResult(false, handleFirebaseException(task.exception))
                }
            }
    }

    fun signUpUser(email: String, password: String, fullName: String, onResult: (Boolean, String?) -> Unit) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    if (user == null) {
                        onResult(false, "Unexpected error: User is null.")
                        return@addOnCompleteListener
                    }
                    updateUserProfile(fullName) { success, errorMessage ->
                        onResult(success, errorMessage)
                    }
                } else {
                    onResult(false, handleFirebaseException(task.exception))
                }
            }
    }

    fun saveUserToFirestore(uid: String, fullName: String, email: String, role: String, onResult: (Boolean) -> Unit) {
        val user = mapOf(
            "fullName" to fullName,
            "email" to email,
            "role" to role
        )
        firestore.collection("users").document(uid).set(user)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { exception ->
                logError("Failed to save user to Firestore", exception)
                onResult(false)
            }
    }

    private fun updateUserProfile(fullName: String, onResult: (Boolean, String?) -> Unit) {
        val user = firebaseAuth.currentUser
        if (user == null) {
            onResult(false, "User not logged in")
            return
        }
        val profileUpdates = userProfileChangeRequest {
            displayName = fullName
        }
        user.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, handleFirebaseException(task.exception))
                }
            }
    }

    fun getUserRoleFromFirestore(uid: String, onResult: (String?) -> Unit) {
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val role = document.getString("role")
                    onResult(role) // Επιτυχία, επιστρέφει τον ρόλο
                } else {
                    onResult(null) // Το έγγραφο δεν βρέθηκε, επιστρέφουμε null
                }
            }
            .addOnFailureListener { exception ->
                logError("Failed to fetch user role from Firestore", exception)
                onResult(null) // Σε περίπτωση σφάλματος, επιστρέφουμε null
            }
    }

    fun deleteUserAccount(onResult: (Boolean, String?) -> Unit) {
        val user = firebaseAuth.currentUser
        if (user == null) {
            onResult(false, "No user is currently logged in.")
            return
        }

        // Get the user's UID to delete their Firestore document
        val uid = user.uid

        // First, delete the Firestore document
        firestore.collection("users").document(uid).delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Then, delete the user from Firebase Authentication
                    user.delete()
                        .addOnCompleteListener { deleteTask ->
                            if (deleteTask.isSuccessful) {
                                onResult(true, null)
                            } else {
                                onResult(false, handleFirebaseException(deleteTask.exception))
                            }
                        }
                } else {
                    logError("Failed to delete Firestore document for user", task.exception)
                    onResult(false, "Failed to delete user data from Firestore.")
                }
            }
    }


    private fun handleFirebaseException(exception: Exception?): String {
        return exception?.localizedMessage ?: "An unexpected error occurred"
    }

    private fun logError(message: String, exception: Exception?) {
        println("Error: $message, Exception: ${exception?.localizedMessage}")
    }

    fun getFullName(): String? {
        return firebaseAuth.currentUser?.displayName
    }
    fun getUserEmail(): String? {
        return firebaseAuth.currentUser?.email
    }

    fun logoutUser() {
        firebaseAuth.signOut()
        isUserLoggedInState.value = false
        fullNameState.value = null
    }

    fun isUserLoggedIn(): Boolean {
        val loggedIn = firebaseAuth.currentUser != null
        Log.d("AuthRepository", "isUserLoggedIn: $loggedIn")
        return firebaseAuth.currentUser != null
    }

}
