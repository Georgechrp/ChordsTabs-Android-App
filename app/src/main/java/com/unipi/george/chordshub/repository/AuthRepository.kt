package com.unipi.george.chordshub.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

object AuthRepository {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun signInUser(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        try {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onResult(true, null)
                    } else {
                        onResult(false, task.exception?.localizedMessage ?: "Login failed")
                    }
                }
        } catch (e: Exception) {
            onResult(false, e.localizedMessage)
        }
    }
    fun saveUserToFirestore(uid: String, fullName: String, email: String, role: String, onResult: (Boolean) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val user = mapOf(
            "fullName" to fullName,
            "email" to email,
            "role" to role
        )
        db.collection("users").document(uid).set(user)
            .addOnSuccessListener {
                onResult(true)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    fun signUpUser(email: String, password: String, fullName: String, onResult: (Boolean, String?) -> Unit) {
        try {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = task.result?.user
                        if (user != null) {
                            updateUserProfile(fullName) { success, errorMessage ->
                                if (success) {
                                    onResult(true, null) // Εγγραφή και ενημέρωση επιτυχής
                                } else {
                                    onResult(false, errorMessage) // Σφάλμα ενημέρωσης
                                }
                            }
                        }
                    } else {
                        onResult(false, task.exception?.message ?: "Sign-up failed") // Σφάλμα εγγραφής
                    }
                }
        } catch (e: Exception) {
            onResult(false, e.localizedMessage ?: "An unexpected error occurred during sign-up")
        }
    }
    fun getUserRoleFromFirestore(uid: String, onResult: (String?) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                val role = document.getString("role")
                onResult(role)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }


    private fun updateUserProfile(fullName: String, onResult: (Boolean, String?) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        val profileUpdates = userProfileChangeRequest {
            displayName = fullName
        }
        user?.updateProfile(profileUpdates)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null) // Επιτυχής ενημέρωση
                } else {
                    onResult(false, task.exception?.message) // Αποτυχία ενημέρωσης
                }
            }
    }

    fun getFullName(): String? {
        val user = FirebaseAuth.getInstance().currentUser
        return user?.displayName
    }



    fun logoutUser() {
        firebaseAuth.signOut()
    }

    fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }
}
