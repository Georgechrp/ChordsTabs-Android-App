package com.unipi.george.chordshub.screens.slidemenu

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.unipi.george.chordshub.R
import com.unipi.george.chordshub.viewmodels.user.SettingsViewModel
import com.unipi.george.chordshub.components.AppText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, settingsViewModel: SettingsViewModel) {
    Scaffold(
        topBar = { SettingsTopBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            DarkModeToggle(settingsViewModel)
            LanguageSelection(settingsViewModel)
            FontSizeSelection(settingsViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTopBar(navController: NavController) {
    TopAppBar(
        title = { Text(stringResource(R.string.settings_text)) },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Πίσω")
            }
        }
    )
}

@Composable
fun DarkModeToggle(settingsViewModel: SettingsViewModel) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        AppText("Dark Mode", settingsViewModel)
        Spacer(modifier = Modifier.weight(1f))
        Switch(
            checked = settingsViewModel.darkMode.value,
            onCheckedChange = { settingsViewModel.toggleDarkMode() }
        )
    }
    Divider(modifier = Modifier.padding(vertical = 4.dp))
}


@Composable
fun LanguageSelection(settingsViewModel: SettingsViewModel) {
    AppText("Γλώσσα", settingsViewModel)
    Row {
        Button(
            onClick = { settingsViewModel.changeLanguage("greek") },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
        ) {
            Text("Ελληνικά", color = Color.White)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = { settingsViewModel.changeLanguage("english") },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("English", color = Color.White)
        }
    }
    Divider(modifier = Modifier.padding(vertical = 4.dp))
}


@Composable
fun FontSizeSelection(settingsViewModel: SettingsViewModel) {
    var tempFontSize by remember { mutableStateOf(settingsViewModel.fontSize.value) }
    var sampleText by remember { mutableStateOf("Δοκιμή Γραμματοσειράς") }

    AppText("Μέγεθος Γραμματοσειράς: ${tempFontSize.toInt()}sp", settingsViewModel)
    Row(verticalAlignment = Alignment.CenterVertically) {
        Slider(
            value = tempFontSize,
            onValueChange = {
                tempFontSize = it
            },
            valueRange = 12f..24f,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = { settingsViewModel.changeFontSize(tempFontSize) }) {
            Icon(Icons.Filled.Save, contentDescription = "Αποθήκευση Μεγέθους Γραμματοσειράς")
        }
    }
    Text(
        text = sampleText,
        style = TextStyle(fontSize = tempFontSize.sp)
    )
}

/*
fun updateLocale(context: Context, language: String) {
    val locale = Locale(language)
    Locale.setDefault(locale)

    val config = Configuration()
    config.setLocale(locale)

    context.resources.updateConfiguration(config, context.resources.displayMetrics)
}
*/
