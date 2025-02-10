package com.unipi.george.chordshub.screens.profile

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.unipi.george.chordshub.repository.AuthRepository

@Composable
fun SettingsScreen(navController: NavController, onLogout: () -> Unit) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val role = remember { mutableStateOf<String?>(null) }
    val deleteMessage = remember { mutableStateOf<String?>(null) }
    val showDeleteDialog = remember { mutableStateOf(false) }
    val showLogoutDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(uid) {
        if (uid != null) {
            AuthRepository.getUserRoleFromFirestore(uid) { userRole ->
                role.value = userRole
            }
        }
    }

    if (showDeleteDialog.value) {
        ConfirmationDialog(
            title = "Delete Account",
            message = "Are you sure you want to delete your account? This action cannot be undone.",
            onConfirm = {
                showDeleteDialog.value = false
                AuthRepository.deleteUserAccount { success, message ->
                    if (success) {
                        Toast.makeText(context, "Account Deleted", Toast.LENGTH_SHORT).show()
                        AuthRepository.isUserLoggedInState.value = false
                    } else {
                        deleteMessage.value = message ?: "An unexpected error occurred."
                    }
                }
            },
            onDismiss = { showDeleteDialog.value = false }
        )
    }

    if (showLogoutDialog.value) {
        ConfirmationDialog(
            title = "Logout",
            message = "Are you sure you want to logout?",
            onConfirm = {
                showLogoutDialog.value = false
                AuthRepository.logoutUser()
                onLogout() // ή οποιαδήποτε άλλη ενέργεια για το logout
            },
            onDismiss = { showLogoutDialog.value = false }
        )
    }

    SettingsContent(
        role = role.value,
        showDeleteDialog = showDeleteDialog,
        deleteMessage = deleteMessage,
        showLogoutDialog = showLogoutDialog
    )
}

@Composable
fun SettingsContent(
    role: String?,
    showDeleteDialog: MutableState<Boolean>,
    deleteMessage: MutableState<String?>,
    showLogoutDialog: MutableState<Boolean>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        //Text("Role: $role")
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(
            onClick = { showLogoutDialog.value = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ExitToApp,
                contentDescription = "Logout Icon",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text("Logout")
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(
            onClick = { showDeleteDialog.value = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Account Icon",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text("Delete Account")
        }
        Spacer(modifier = Modifier.height(16.dp))
        deleteMessage.value?.let { message ->
            Text(text = message)
        }
    }
}


@Composable
fun ConfirmationDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
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

