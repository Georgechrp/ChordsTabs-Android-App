package com.unipi.george.chordshub


import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.unipi.george.chordshub.navigation.BottomNavigationBar
import com.unipi.george.chordshub.navigation.Screen
import com.unipi.george.chordshub.repository.AuthRepository
import com.unipi.george.chordshub.screens.HomeScreen
import com.unipi.george.chordshub.screens.LoginScreen
import com.unipi.george.chordshub.screens.ProfileScreen
import com.unipi.george.chordshub.screens.SettingsScreen
import com.unipi.george.chordshub.screens.SignUpScreen
import com.unipi.george.chordshub.ui.theme.ChordsHubTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChordsHubTheme {
                val navController = rememberNavController()
                val isUserLoggedInState = AuthRepository.isUserLoggedInState
                val fullNameState = AuthRepository.fullNameState

                if (isUserLoggedInState.value) {
                    //Log.d("MainActivity", "isUserLoggedIn: ${isUserLoggedInState.value}")
                    LoggedInScaffold(navController, fullNameState, isUserLoggedInState)
                } else {
                    //Log.d("MainActivity", "User is NOT logged in. Navigating to LoggedOutNavHost.")
                    LoggedOutNavHost(navController, isUserLoggedInState, fullNameState)
                }
            }
        }
    }
}

@Composable
fun LoggedInScaffold(
    navController: NavHostController,
    fullNameState: MutableState<String?>,
    isUserLoggedInState: MutableState<Boolean>
) {
    Scaffold(
        topBar = {
            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
            Log.d("LoggedInScaffold", "Current route: ${navController.currentBackStackEntryAsState().value?.destination?.route}")

            TopBar(
                fullName = fullNameState.value ?: "User",
                painter = painterResource(id = R.drawable.user_icon),
                navController = navController,
                isVisible = currentRoute != "Login" && currentRoute != "SignUp"
            )
        },
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(navController)
            }
            composable(Screen.Profile.route) {
                ProfileScreen(navController)
            }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    navController,
                    onLogout = {
                        isUserLoggedInState.value = false
                        fullNameState.value = null
                        AuthRepository.logoutUser()
                    }
                )
            }
        }
    }
}


@Composable
fun LoggedOutNavHost(
    navController: NavHostController,
    isUserLoggedInState: MutableState<Boolean>,
    fullNameState: MutableState<String?>
) {
    NavHost(
        navController = navController,
        startDestination = "Login"
    ) {
        composable("Login") {
            LoginScreen(navController) {
                isUserLoggedInState.value = true
                fullNameState.value = AuthRepository.getFullName()
            }
        }
        composable("SignUp") {
            SignUpScreen(navController)
        }
    }
}



@Composable
fun TopBar(
    fullName: String,
    painter: Painter,
    navController: NavController,
    isVisible: Boolean
) {
    if (isVisible) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.hello_user, fullName),
                style = MaterialTheme.typography.bodyLarge
            )

            CircularImageView(painter = painter, onClick = {
                //Log.d("TopBar", "Navigating to route: ${Screen.Slide.route}")
                //navController.navigate(Screen.Slide.route)
            })
        }
    } else {
        Spacer(modifier = Modifier.height(0.dp))
    }
}



@Composable
fun CircularImageView(painter: Painter, onClick: () -> Unit) {
    Image(
        painter = painter,
        stringResource(R.string.circular_image_description),
        modifier = Modifier
            .size(50.dp) // Προσαρμόζεις το μέγεθος της εικόνας
            .clip(CircleShape) // Στρογγυλό σχήμα
            .border(2.dp, Color.Gray, CircleShape)
            .clickable { onClick() }
    )
}