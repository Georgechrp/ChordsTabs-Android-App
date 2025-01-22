package com.unipi.george.chordshub.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.unipi.george.chordshub.repository.AuthRepository

@Composable
fun HomeScreen(navController: NavController) {
    val fullName = AuthRepository.getFullName()
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val role = remember { mutableStateOf<String?>(null) }
    // Ανάκτηση του ρόλου από Firestore
    LaunchedEffect(uid) {
        if (uid != null) {
            AuthRepository.getUserRoleFromFirestore(uid) { userRole ->
                role.value = userRole
            }
        }
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text("Welcome $fullName !")

        Spacer(modifier = Modifier.height(16.dp))
        Text("Your role: ${role.value ?: "Loading..."}")
        Button(onClick = {
            AuthRepository.logoutUser() // Λειτουργία αποσύνδεσης
            navController.navigate("Login") {
                popUpTo("Home") { inclusive = true } // Αφαιρεί το HomeScreen από τη στοίβα
            }
        }) {
            Text("Logout")
        }
    }



}