package com.unipi.george.chordshub.viewmodels.user


import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.unipi.george.chordshub.sharedpreferences.UserPreferences
import com.unipi.george.chordshub.components.AppText

class SettingsViewModel(private val userPreferences: UserPreferences) : ViewModel() {
    val darkMode = mutableStateOf(userPreferences.isDarkMode())
    val language = mutableStateOf(userPreferences.getLanguage())
    val fontSize = mutableFloatStateOf(userPreferences.getFontSize())

    var onThemeChange: (() -> Unit)? = null // Callback για το Activity

    fun toggleDarkMode() {
        darkMode.value = !darkMode.value
        userPreferences.setDarkMode(darkMode.value)

        onThemeChange?.invoke()
    }

    fun changeLanguage(newLanguage: String) {
        language.value = newLanguage
        userPreferences.setLanguage(newLanguage)
    }

    fun changeFontSize(newSize: Float) {
        fontSize.value = newSize
        userPreferences.setFontSize(newSize)
    }

   /* @Composable
    fun AppText(text: String, settingsViewModel: SettingsViewModel, modifier: Modifier = Modifier) {
        Text(
            text = text,
            style = TextStyle(fontSize = settingsViewModel.fontSize.value.sp),
            modifier = modifier
        )
    }*/

}
