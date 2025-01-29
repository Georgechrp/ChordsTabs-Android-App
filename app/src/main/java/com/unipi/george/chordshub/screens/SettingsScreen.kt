package com.unipi.george.chordshub.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.unipi.george.chordshub.navigation.Screen
import com.unipi.george.chordshub.repository.AuthRepository
import com.unipi.george.chordshub.repository.AuthRepository.isUserLoggedInState

@Composable
fun SettingsScreen(navController: NavController, onLogout: () -> Unit) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val role = remember { mutableStateOf<String?>(null) }
    val deleteMessage = remember { mutableStateOf<String?>(null) }
    val showDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(uid) {
        if (uid != null) {
            AuthRepository.getUserRoleFromFirestore(uid) { userRole ->
                role.value = userRole
            }
        }
    }

    if (showDialog.value) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("Delete Account") },
            text = { Text("Are you sure you want to delete your account? This action cannot be undone.") },
            confirmButton = {
                Button(onClick = {
                    showDialog.value = false
                    AuthRepository.deleteUserAccount { success, message ->
                        if (success) {
                            Toast.makeText(context, "Account Deleted", Toast.LENGTH_SHORT).show()
                            isUserLoggedInState.value = false
                        } else {
                            deleteMessage.value = message ?: "An unexpected error occurred."
                        }
                    }
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog.value = false }) {
                    Text("No")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text("Role: ${role.value}")
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            AuthRepository.logoutUser()
        }) {
            Text("Logout")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            showDialog.value = true // Εμφάνιση του παραθύρου διαλόγου
        }) {
            Text("Delete Account")
        }
        Spacer(modifier = Modifier.height(16.dp))
        deleteMessage.value?.let { message ->
            Text(text = message)
        }
    }
}

