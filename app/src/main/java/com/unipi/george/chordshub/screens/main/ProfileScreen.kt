package com.unipi.george.chordshub.screens.main

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
import com.google.firebase.auth.FirebaseAuth
import com.unipi.george.chordshub.repository.AuthRepository

@Composable
fun ProfileScreen(onLogout: () -> Unit) {
    val fullName = AuthRepository.getFullName()
    val email = AuthRepository.getUserEmail()
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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.Top
        ) {
            ProfileCard(fullName, email, showLogoutDialog, showDeleteDialog, deleteMessage)
        }
    }

    if (showDeleteDialog.value) {
        ConfirmActionDialog(
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
        ConfirmActionDialog(
            title = "Logout",
            message = "Are you sure you want to logout?",
            onConfirm = {
                showLogoutDialog.value = false
                AuthRepository.logoutUser()
                onLogout()
            },
            onDismiss = { showLogoutDialog.value = false }
        )
    }
}


@Composable
fun ProfileCard(
    fullName: String?,
    email: String?,
    showLogoutDialog: MutableState<Boolean>,
    showDeleteDialog: MutableState<Boolean>,
    deleteMessage: MutableState<String?>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            InfoRow(label = "Full Name", value = fullName)
            Divider(modifier = Modifier.padding(vertical = 4.dp))
            InfoRow(label = "Email", value = email)

            Spacer(modifier = Modifier.height(16.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Spacer(modifier = Modifier.height(16.dp))

            // Logout Button
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

            // Delete Account Button
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
                Text(text = message, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String?) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value ?: "-",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun ConfirmActionDialog(
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
            Button(onClick = onConfirm) { Text("Yes") }
        },
        dismissButton = {
            Button(onClick = onDismiss) { Text("No, Thanks") }
        }
    )
}
