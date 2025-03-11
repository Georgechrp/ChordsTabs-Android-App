package com.unipi.george.chordshub.screens.auth

import android.util.Log
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.unipi.george.chordshub.R
import com.unipi.george.chordshub.navigation.Screen
import com.unipi.george.chordshub.repository.AuthRepository
import com.unipi.george.chordshub.repository.AuthRepository.isUserLoggedInState

@Composable
fun LoginScreen(navController: NavController, onLoginSuccess: () -> Unit) {
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
        Text(
            text = "Welcome",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        TextField(
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text(stringResource(R.string.email)) },
            singleLine = true,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text(stringResource(R.string.password)) },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            maxLines = 1
        )
        TextButton(onClick = {
            navController.navigate(Screen.ForgotPassword.route)
        }) {
            Text(stringResource(R.string.resetPassword_text))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            if (email.value.isBlank() || password.value.isBlank()) {
                Toast.makeText(context, context.getString(R.string.please_fill_fields), Toast.LENGTH_SHORT).show()
                return@Button
            }

            AuthRepository.signInUser(email.value, password.value) { success, errorMessage ->
                if (success) {
                    Log.d("LoginScreen", "Login successful. Navigating to Home: ${Screen.Home.route}")
                    onLoginSuccess()
                    isUserLoggedInState.value = true
                } else {
                    Toast.makeText(context, errorMessage ?: context.getString(R.string.login_failed), Toast.LENGTH_SHORT).show()
                }
            }
        }) {
            Text(stringResource(R.string.sign_in))
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = {
            navController.navigate(Screen.SignUp.route)
        }) {
            Text(stringResource(R.string.dont_have_account))
        }
    }
}

