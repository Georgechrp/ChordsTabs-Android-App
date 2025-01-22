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
import com.unipi.george.chordshub.repository.AuthRepository

@Composable
fun LoginScreen(navController: NavController) {
    // Κατάσταση για τα πεδία
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = email.value,
            onValueChange = { email.value = it }, // Ενημέρωση της κατάστασης
            label = { Text("Email") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password.value,
            onValueChange = { password.value = it }, // Ενημέρωση της κατάστασης
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            if (email.value.isBlank() || password.value.isBlank()) {
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@Button
            }

            AuthRepository.signInUser(email.value, password.value) { success, errorMessage ->
                if (success) {
                    navController.navigate("Home"){
                        popUpTo("Login") { inclusive = true } // Αφαιρεί τη LoginScreen από τη στοίβα
                    }
                    Toast.makeText(context,  "Welcome", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, errorMessage ?: "Login failed", Toast.LENGTH_SHORT).show()
                }
            }


        }) {
            Text("Sign In")
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = {
            navController.navigate("SignUp")
        }) {
            Text("Don't have an account? Sign Up")
        }
    }
}
