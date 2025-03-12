package com.unipi.george.chordshub.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.unipi.george.chordshub.viewmodels.user.SettingsViewModel

@Composable
fun AppText(text: String, settingsViewModel: SettingsViewModel, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = TextStyle(fontSize = settingsViewModel.fontSize.value.sp),
        modifier = modifier
    )
}