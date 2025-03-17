package com.unipi.george.chordshub.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.unipi.george.chordshub.viewmodels.user.SettingsViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

@Composable
fun LoadingView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(modifier = Modifier.size(50.dp))
    }
}

@Composable
fun AppText(text: String, settingsViewModel: SettingsViewModel, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = TextStyle(fontSize = settingsViewModel.fontSize.value.sp),
        modifier = modifier
    )
}


