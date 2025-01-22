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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.unipi.george.chordshub.repository.AuthRepository
import com.unipi.george.chordshub.repository.AuthRepository.saveUserToFirestore

@Composable
fun SignUpScreen(navController: NavController) {
    // Καταστάσεις για τα πεδία
    val fullName = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Πεδίο Full Name
        TextField(
            value = fullName.value,
            onValueChange = { fullName.value = it },
            label = { Text("Full Name") }
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Πεδίο Email
        TextField(
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text("Email") }
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Πεδίο Password
        TextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Πεδίο Confirm Password
        TextField(
            value = confirmPassword.value,
            onValueChange = { confirmPassword.value = it },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Κουμπί Create Account
        Button(onClick = {
            if (fullName.value.isBlank() || email.value.isBlank() || password.value.isBlank() || confirmPassword.value.isBlank()) {
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@Button
            }

            if (password.value != confirmPassword.value) {
                Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@Button
            }

            AuthRepository.signUpUser(email.value, password.value, fullName.value) { success, errorMessage ->
                if (success) {
                    val uid = FirebaseAuth.getInstance().currentUser?.uid
                    if (uid != null) {
                        saveUserToFirestore(uid, fullName.value, email.value, "user") { firestoreSuccess ->
                            if (firestoreSuccess) {
                                Toast.makeText(context, "Account created successfully!", Toast.LENGTH_SHORT).show()
                                navController.navigate("Login")
                            } else {
                                Toast.makeText(context, "Failed to save user role", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(context, errorMessage ?: "Sign-up failed", Toast.LENGTH_SHORT).show()
                }
            }

        }) {
            Text("Create Account")
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Κουμπί πλοήγησης στο Login
        TextButton(onClick = {
            navController.navigate("Login")
        }) {
            Text("Already have an account? Sign In")
        }
    }
}

