package com.unipi.george.chordshub.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@Composable
fun ProfileSettings(
    isMenuOpen: MutableState<Boolean>,
    navController: NavController,
    onLogout: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val selectedTab = remember { mutableStateOf("Profile") }

    val dragOffset = remember { mutableStateOf(0f) } // Κρατάει την τρέχουσα μετατόπιση
    val screenWidth =
        with(LocalDensity.current) { LocalConfiguration.current.screenWidthDp.dp.toPx() } // Παίρνει το πλάτος της οθόνης
    val maxDrag = -screenWidth * 0.70f // Μέγιστο όριο drag (70% από την αριστερή πλευρά)

    val animatedOffset by animateFloatAsState(
        targetValue = dragOffset.value,
        animationSpec = tween(durationMillis = 200)
    )

    AnimatedVisibility(
        visible = isMenuOpen.value,
        enter = slideInHorizontally(
            initialOffsetX = { -it },
            animationSpec = tween(durationMillis = 300)
        ),
        exit = slideOutHorizontally(
            targetOffsetX = { -it },
            animationSpec = tween(durationMillis = 300)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.85f)
                .offset { IntOffset(animatedOffset.toInt(), 0) } // Χρησιμοποιούμε το animatedOffset
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { _, dragAmount ->
                            dragOffset.value = (dragOffset.value + dragAmount)
                                .coerceIn(
                                    maxDrag,
                                    0f
                                ) // Περιορίζει την κίνηση από 0 έως -70% του πλάτους της οθόνης
                        },
                        onDragEnd = {
                            scope.launch {
                                if (dragOffset.value < maxDrag * 0.35f) {
                                    isMenuOpen.value = false
                                    dragOffset.value = 0f // Reset του offset αμέσως
                                } else {
                                    // Αν δεν το έχει σύρει αρκετά, επιστρέφει στη θέση του με animation
                                    animate(
                                        initialValue = dragOffset.value,
                                        targetValue = 0f,
                                        animationSpec = tween(durationMillis = 200)
                                    ) { value, _ ->
                                        dragOffset.value = value
                                    }
                                }
                            }
                        }
                    )
                },
            contentAlignment = Alignment.TopStart
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { selectedTab.value = "Profile" },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = if (selectedTab.value == "Profile") MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    ) {
                        Text("Profile")
                    }
                    Button(
                        onClick = { selectedTab.value = "Settings" },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = if (selectedTab.value == "Settings") MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    ) {
                        Text("Settings")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (selectedTab.value == "Profile") {
                    ProfileScreen(navController = navController)
                } else {
                    SettingsScreen(navController = navController, onLogout = onLogout)
                }
            }
        }
    }
}

