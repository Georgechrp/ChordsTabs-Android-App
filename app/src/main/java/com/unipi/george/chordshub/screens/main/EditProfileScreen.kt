package com.unipi.george.chordshub.screens.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.unipi.george.chordshub.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: NavController, currentUsername: String, currentProfileImage: Int) {
    var newUsername by remember { mutableStateOf(currentUsername) }
    var selectedImage by remember { mutableStateOf(currentProfileImage) }


    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center // Κεντράρει την κάρτα στη μέση της οθόνης
    ) {
        Card(
            modifier = Modifier
                .width(320.dp) // Περιορίζει το πλάτος της κάρτας
                .padding(24.dp), // Μειώνει το padding για πιο compact εμφάνιση
            shape = RoundedCornerShape(8.dp), // Κάνει την κάρτα πιο τετραγωνική
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Προβολή εικόνας προφίλ
                Image(
                    painter = painterResource(id = selectedImage),
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .clickable {
                            // TODO: Άνοιγμα Image Picker
                            selectedImage = R.drawable.edit_user_image // Προσωρινή επιλογή
                        }
                )

                // Πεδίο για το Username
                OutlinedTextField(
                    value = newUsername,
                    onValueChange = { newUsername = it },
                    label = { Text("Username") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Κουμπί αποθήκευσης
                Button(
                    onClick = {
                        // TODO: Αποθήκευση στο Firestore ή τοπική βάση
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            "newUsername",
                            newUsername
                        )
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            "newImage",
                            selectedImage
                        )
                        navController.popBackStack()
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Changes")
                }

                // Κουμπί ακύρωσης
                TextButton(onClick = { navController.popBackStack() }) {
                    Text("Cancel", color = Color.Gray)
                }
            }

        }
    }
}
