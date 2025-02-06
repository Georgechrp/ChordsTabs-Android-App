package com.unipi.george.chordshub.screens

import android.content.Context
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.unipi.george.chordshub.R
import com.unipi.george.chordshub.navigation.Screen
import com.unipi.george.chordshub.repository.AuthRepository
import com.unipi.george.chordshub.repository.AuthRepository.saveUserToFirestore

@Composable
fun SignUpScreen(navController: NavController) {
    // Αρχικοποίηση των states
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
        SignUpInputFields(
            fullName = fullName,
            email = email,
            password = password,
            confirmPassword = confirmPassword
        )
        Spacer(modifier = Modifier.height(16.dp))

        SignUpActions(
            fullName = fullName,
            email = email,
            password = password,
            confirmPassword = confirmPassword,
            navController = navController,
            context = context
        )
    }
}

@Composable
fun SignUpInputFields(
    fullName: MutableState<String>,
    email: MutableState<String>,
    password: MutableState<String>,
    confirmPassword: MutableState<String>
) {
    TextField(
        value = fullName.value,
        onValueChange = { fullName.value = it },
        label = { Text(stringResource(R.string.full_name)) }
    )
    Spacer(modifier = Modifier.height(8.dp))

    TextField(
        value = email.value,
        onValueChange = { email.value = it },
        label = { Text(stringResource(R.string.email)) }
    )
    Spacer(modifier = Modifier.height(8.dp))

    TextField(
        value = password.value,
        onValueChange = { password.value = it },
        label = { Text("Password") },
        visualTransformation = PasswordVisualTransformation()
    )
    Spacer(modifier = Modifier.height(8.dp))

    TextField(
        value = confirmPassword.value,
        onValueChange = { confirmPassword.value = it },
        label = { Text("Confirm Password") },
        visualTransformation = PasswordVisualTransformation()
    )
}

@Composable
fun SignUpActions(
    fullName: MutableState<String>,
    email: MutableState<String>,
    password: MutableState<String>,
    confirmPassword: MutableState<String>,
    navController: NavController,
    context: Context
) {
    Button(onClick = {
        if (fullName.value.isBlank() || email.value.isBlank() || password.value.isBlank() || confirmPassword.value.isBlank()) {
            Toast.makeText(context, context.getString(R.string.please_fill_fields), Toast.LENGTH_SHORT).show()
            return@Button
        }

        if (password.value != confirmPassword.value) {
            Toast.makeText(context, context.getString(R.string.passwords_do_not_match), Toast.LENGTH_SHORT).show()
            return@Button
        }

        AuthRepository.signUpUser(email.value, password.value, fullName.value) { success, errorMessage ->
            if (success) {
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                if (uid != null) {
                    saveUserToFirestore(uid, fullName.value, email.value, "user") { firestoreSuccess ->
                        if (firestoreSuccess) {
                            Toast.makeText(context, context.getString(R.string.account_created_successfully), Toast.LENGTH_SHORT).show()
                            navController.navigate(Screen.Login.route)
                        } else {
                            Toast.makeText(context, context.getString(R.string.failed_to_save_user_role), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(context, errorMessage ?: context.getString(R.string.sign_up_failed), Toast.LENGTH_SHORT).show()
            }
        }
    }) {
        Text(stringResource(R.string.create_account))
    }
    Spacer(modifier = Modifier.height(8.dp))

    TextButton(onClick = {
        Log.d("SignUpScreen", "Navigating to Login: ${Screen.Login.route}")
        navController.navigate(Screen.Login.route)
    }) {
        Text(stringResource(R.string.already_have_account))
    }
}
