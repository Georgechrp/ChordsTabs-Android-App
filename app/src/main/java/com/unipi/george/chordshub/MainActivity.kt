package com.unipi.george.chordshub


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.unipi.george.chordshub.navigation.BottomNavigationBar
import com.unipi.george.chordshub.navigation.Screen
import com.unipi.george.chordshub.repository.AuthRepository
import com.unipi.george.chordshub.screens.HomeScreen
import com.unipi.george.chordshub.screens.LibraryScreen
import com.unipi.george.chordshub.screens.LoginScreen
import com.unipi.george.chordshub.screens.ProfileScreen
import com.unipi.george.chordshub.screens.SearchScreen
import com.unipi.george.chordshub.screens.SettingsScreen
import com.unipi.george.chordshub.screens.SignUpScreen
import com.unipi.george.chordshub.ui.theme.ChordsHubTheme
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChordsHubTheme {
                val navController = rememberNavController()
                val isUserLoggedInState = AuthRepository.isUserLoggedInState
                val fullNameState = AuthRepository.fullNameState

                if (isUserLoggedInState.value) {
                    LoggedInScaffold(navController, fullNameState, isUserLoggedInState)
                } else {
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
    val isMenuOpen = remember { mutableStateOf(false) }
    val isFullScreen = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Όταν είναι fullscreen, το offset παραμένει στο 0 (δεν μετακινείται η κύρια οθόνη)
    val screenOffset = animateDpAsState(
        targetValue = if (isMenuOpen.value && !isFullScreen.value) 200.dp else 0.dp,
        animationSpec = tween(durationMillis = 300), label = ""
    )

    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = screenOffset.value) // Μετακίνηση μόνο αν δεν είναι fullscreen
        ) {
            TopBar(
                fullName = fullNameState.value ?: "User",
                painter = painterResource(id = R.drawable.user_icon),
                navController = navController,
                isVisible = !isFullScreen.value, // Κρύβει το TopBar αν είναι fullscreen
                onMenuClick = { isMenuOpen.value = !isMenuOpen.value }
            )

            Scaffold(
                bottomBar = {
                    if (!isFullScreen.value) { // Κρύβει το BottomNav αν είναι fullscreen
                        BottomNavigationBar(navController)
                    }
                }
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route,
                    modifier = Modifier.padding(innerPadding)
                ) {
                    composable(Screen.Home.route) {
                        HomeScreen(
                            navController = navController,
                            isFullScreen = isFullScreen.value,
                            onFullScreenChange = { isFullScreen.value = it } // Ελέγχει αν είναι fullscreen
                        )
                    }
                    composable(Screen.Search.route) {
                        SearchScreen(navController)
                    }
                    composable(Screen.Library.route) {
                        LibraryScreen(navController)
                    }
                }
            }
        }

        // Slide Menu που εμφανίζεται ΜΟΝΟ αν δεν είναι fullscreen
        if (!isFullScreen.value) {
            ProfileSettings(isMenuOpen = isMenuOpen, navController = navController, onLogout = {
                isUserLoggedInState.value = false
                fullNameState.value = null
                AuthRepository.logoutUser()
            })
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
    isVisible: Boolean,
    onMenuClick: () -> Unit
) {
    if (isVisible) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
            //horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CircularImageView(painter = painter, onClick = {
                onMenuClick()
            })
            Text(
                text = "Hello, $fullName",
                style = MaterialTheme.typography.bodyLarge
            )


        }
    }
}




@Composable
fun CircularImageView(painter: Painter, onClick: () -> Unit) {
    Image(
        painter = painter,
        stringResource(R.string.circular_image_description),
        modifier = Modifier
            .size(30.dp)
            .clip(CircleShape)
            .border(2.dp, Color.Gray, CircleShape)
            .clickable { onClick() }
    )
}

@Composable
fun ProfileSettings(isMenuOpen: MutableState<Boolean>, navController: NavController, onLogout: () -> Unit) {
    val scope = rememberCoroutineScope()
    val selectedTab = remember { mutableStateOf("Profile") } // **Επιλογή ανάμεσα σε Profile & Settings**

    AnimatedVisibility(
        visible = isMenuOpen.value,
        enter = slideInHorizontally(
            initialOffsetX = { -it }, // Έρχεται από τα αριστερά
            animationSpec = tween(durationMillis = 300)
        ),
        exit = slideOutHorizontally(
            targetOffsetX = { -it }, // Εξαφανίζεται προς τα αριστερά
            animationSpec = tween(durationMillis = 300)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.85f) // 85% του πλάτους της οθόνης
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { _, dragAmount ->
                            if (dragAmount < -50) { // Αν το drag είναι προς τα αριστερά
                                scope.launch {
                                    isMenuOpen.value = false // Κλείνει το μενού
                                }
                            }
                        }
                    )
                },
            contentAlignment = Alignment.TopStart
        ) {
            Column {
                // tabs για εναλλαγή μεταξύ Profile - Settings
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
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

                // Εμφανίζει Profile ή Settings ανάλογα με το επιλεγμένο tab
                if (selectedTab.value == "Profile") {
                    ProfileScreen(navController = navController)
                } else {
                    SettingsScreen(navController = navController, onLogout = onLogout)
                }
            }
        }
    }
}

