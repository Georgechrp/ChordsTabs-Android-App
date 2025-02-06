package com.unipi.george.chordshub.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
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
        DeleteAccountDialog(
            onConfirm = {
                showDialog.value = false
                AuthRepository.deleteUserAccount { success, message ->
                    if (success) {
                        Toast.makeText(context, "Account Deleted", Toast.LENGTH_SHORT).show()
                        isUserLoggedInState.value = false
                    } else {
                        deleteMessage.value = message ?: "An unexpected error occurred."
                    }
                }
            },
            onDismiss = { showDialog.value = false }
        )
    }

    SettingsContent(role.value, showDialog, deleteMessage)
}

@Composable
fun DeleteAccountDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Account") },
        text = { Text("Are you sure you want to delete your account? This action cannot be undone.") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Yes")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("No")
            }
        }
    )
}

@Composable
fun SettingsContent(role: String?, showDialog: MutableState<Boolean>, deleteMessage: MutableState<String?>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Role: $role")
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { AuthRepository.logoutUser() }) {
            Text("Logout")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { showDialog.value = true }) {
            Text("Delete Account")
        }
        Spacer(modifier = Modifier.height(16.dp))
        deleteMessage.value?.let { message ->
            Text(text = message)
        }
    }
}
