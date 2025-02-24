package com.unipi.george.chordshub.screens.seconds


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.unipi.george.chordshub.repository.AuthRepository

@Composable
fun ProfileMenu(isMenuOpen: MutableState<Boolean>, navController: NavController) {
    AnimatedVisibility(visible = isMenuOpen.value) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    onClick = { isMenuOpen.value = false },
                    indication = null,
                    interactionSource = androidx.compose.foundation.interaction.MutableInteractionSource()
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .blur(16.dp)
                    .background(Color.Black.copy(alpha = 0.2f))
            )
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                EditProfileScreen(
                    navController = navController,
                    userId = AuthRepository.getUserId() ?: "",
                    onDismiss = { isMenuOpen.value = false }
                )
            }
        }
    }
}
