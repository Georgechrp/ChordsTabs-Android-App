package com.unipi.george.chordshub.screens.slidemenu

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.unipi.george.chordshub.R
import com.unipi.george.chordshub.navigation.AppScreens
import com.unipi.george.chordshub.repository.AuthRepository
import com.unipi.george.chordshub.viewmodels.MainViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.unipi.george.chordshub.screens.slidemenu.viewprofile.getProfileImageUrl
import com.unipi.george.chordshub.utils.updateUserProfileImage
import com.unipi.george.chordshub.utils.uploadImageToCloudinary
import kotlinx.coroutines.launch

@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun ProfileMenu(mainViewModel: MainViewModel, navController: NavController) {
    val isMenuOpen by mainViewModel.isMenuOpen

    if (isMenuOpen) {
        BlurredBackground { mainViewModel.setMenuOpen(false) }
    }

    AnimatedVisibility(
        visible = isMenuOpen,
        enter = slideInHorizontally { -it },
        exit = slideOutHorizontally { -it }
    ) {
        ProfileMenuContent(mainViewModel, navController)
    }

}

@Composable
fun BlurredBackground(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .blur(20.dp)
            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                onClick()
            }
    )

}


@Composable
fun ProfileMenuContent(mainViewModel: MainViewModel, navController: NavController) {
    val userId = AuthRepository.getUserId()
    val username = remember { mutableStateOf("Loading...") }
    var profileImageUrl by remember { mutableStateOf<String?>(null) }
    var selectedImage by remember { mutableStateOf<Uri?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(userId) {
        userId?.let {
            AuthRepository.getUsernameFromFirestore(it) { fetchedUsername ->
                username.value = fetchedUsername ?: "Unknown"
            }
            getProfileImageUrl(it)?.let { url ->
                profileImageUrl = url
                mainViewModel.setProfileImageUrl(url)
            }
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            selectedImage = selectedUri

            coroutineScope.launch {
                val imageUrl = uploadImageToCloudinary(selectedUri, context)
                if (imageUrl != null && userId != null) {
                    updateUserProfileImage(userId, imageUrl)
                    profileImageUrl = imageUrl
                    mainViewModel.setProfileImageUrl(imageUrl)
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectHorizontalDragGestures { _, dragAmount ->
                    if (dragAmount < -50) {
                        mainViewModel.setMenuOpen(false)
                    }
                }
            }
        ,
        contentAlignment = Alignment.CenterStart
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.85f)
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = if (selectedImage != null) {
                        rememberAsyncImagePainter(selectedImage)
                    } else if (profileImageUrl != null) {
                        rememberAsyncImagePainter(profileImageUrl)
                    } else {
                        painterResource(id = R.drawable.edit_user_image)
                    },
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .clickable { launcher.launch("image/*") },
                    contentScale = ContentScale.Crop
                )
                UserProfileSection(username, mainViewModel, navController)
            }
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            MenuItem(
                icon = Icons.Filled.AddCircleOutline,
                text = stringResource(R.string.add_song_text),
                route = AppScreens.Upload.route,
                mainViewModel = mainViewModel,
                navController = navController
            )
            MenuItem(
                icon = Icons.Filled.History,
                text = stringResource(R.string.recent_text),
                route = AppScreens.Recents.route,
                mainViewModel = mainViewModel,
                navController = navController
            )
            MenuItem(
                icon = Icons.Filled.Settings,
                text = stringResource(R.string.settings_text),
                route = AppScreens.Settings.route,
                mainViewModel = mainViewModel,
                navController = navController
            )
        }
    }
}



@Composable
fun UserProfileSection(
    username: MutableState<String>,
    mainViewModel: MainViewModel,
    navController: NavController
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(0.dp),
        modifier = Modifier.padding(start = 8.dp)
    ) {
        Text(
            text = username.value,
            style = MaterialTheme.typography.headlineMedium
        )
        TextButton(
            onClick = {
                mainViewModel.setMenuOpen(false)
                navController.navigate(AppScreens.Profile.route)
            },
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.height(20.dp)
        ) {
            Text(stringResource(R.string.view_profile_text), style = MaterialTheme.typography.bodySmall)
        }
    }

}


@Composable
fun MenuItem(
    icon: ImageVector,
    text: String,
    route: String,
    mainViewModel: MainViewModel,
    navController: NavController
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        TextButton(
            onClick = {
                mainViewModel.setMenuOpen(false)
                navController.navigate(route)
            },
            contentPadding = PaddingValues(0.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(icon, contentDescription = text, modifier = Modifier.size(24.dp))
                Text(text = text, fontSize = 16.sp)
            }
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}

