package com.unipi.george.chordshub.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,

    background = Color(0xFF121212), // Σκούρο γκρι για το φόντο
    surface = Color(0xFF1E1E1E), // Ελαφρώς πιο φωτεινό σκούρο για το Card
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color.White, // Κείμενο πάνω στο φόντο
    onSurface = Color.White // Κείμενο πάνω στο Card
)

private val LightColorScheme = lightColorScheme(
    primary = Blue40,  // ✅ Πιο μοντέρνο μπλε (Material 3)
    secondary = Green40, // ✅ Κυανό-πράσινο για contrast
    tertiary = Red40,  // ✅ Κόκκινο για δυναμικά στοιχεία

    background = BackgroundLight, // ✅ Καθαρό λευκό
    surface = SurfaceLight, // ✅ Ανοιχτό γκρι για καλύτερη αισθητική
    onPrimary = OnPrimary,
    onSecondary = OnSecondary,
    onTertiary = OnPrimary,
    onBackground = OnBackgroundLight,
    onSurface = OnSurfaceLight
)



@Composable
fun ChordsHubTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}