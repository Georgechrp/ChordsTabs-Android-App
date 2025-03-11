package com.unipi.george.chordshub.navigation.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import com.unipi.george.chordshub.navigation.Screen

@Composable
fun MainBottomNavBar(navController: NavController) {
    val items = listOf(
        Screen.Home,
        Screen.Search,
        Screen.Library
    )

    Surface( // ➡️ Surface για καλύτερη διαχείριση διαφάνειας
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        color = Color.Transparent, // Transparent Background
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { screen ->
                val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                val isSelected = currentRoute == screen.route

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            navController.navigate(screen.route) {
                                popUpTo(screen.route) { inclusive = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                        .padding(vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = when (screen) {
                            is Screen.Home -> Icons.Filled.Home
                            is Screen.Search -> Icons.Filled.Search
                            is Screen.Library -> Icons.Filled.LibraryMusic
                            else -> Icons.Filled.Home
                        },
                        contentDescription = screen.route,
                        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.size(28.dp)
                    )

                    AnimatedVisibility(visible = isSelected) {
                        Box(
                            modifier = Modifier
                                .width(24.dp)
                                .height(3.dp)
                                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                        )
                    }
                }
            }
        }
    }
}
