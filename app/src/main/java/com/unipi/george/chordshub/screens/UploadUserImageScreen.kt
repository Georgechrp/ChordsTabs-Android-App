package com.unipi.george.chordshub.screens

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

@Composable
fun UploadUserImageScreen(onClose: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                imageUri = uri
            }
        }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.width(16.dp))


            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }

        imageUri?.let { uri ->
            Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = "Selected Image",
                modifier = Modifier
                    .size(150.dp)
                    .padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))


        Button(onClick = { imagePickerLauncher.launch("image/*") }) {
            Text("Choose Image")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                imageUri?.let { uri ->
                    coroutineScope.launch {
                        uploadImageToCloudinary(context, uri) { uploadedUrl ->
                            if (uploadedUrl != null) {
                                Log.d("Cloudinary", "Uploaded Image URL: $uploadedUrl")
                                onClose()
                            } else {
                                Log.e("Cloudinary", "Upload failed")
                            }
                        }
                    }
                } ?: Log.e("Cloudinary", "No image selected")
            }
        ) {
            Text("Upload Image")
        }
    }
}


suspend fun uploadImageToCloudinary(context: Context, imageUri: Uri, callback: (String?) -> Unit) {
    withContext(Dispatchers.IO) {
        try {
            val cloudinary = Cloudinary(mapOf(
                "cloud_name" to "ddqf5osur",
                "api_key" to "139894567327143",
                "api_secret" to "UzTkVWmfuu9DkJgU0RXFY4vMSn4"
            ))

            val file = getFileFromUri(context, imageUri)
            val result = cloudinary.uploader().upload(file, ObjectUtils.emptyMap())
            val imageUrl = result["secure_url"] as? String
            callback(imageUrl)
        } catch (e: Exception) {
            e.printStackTrace()
            callback(null)
        }
    }
}

fun getFileFromUri(context: Context, uri: Uri): File {
    val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
    val tempFile = File(context.cacheDir, "temp_image")
    val outputStream = FileOutputStream(tempFile)
    inputStream?.copyTo(outputStream)
    inputStream?.close()
    outputStream.close()
    return tempFile
}
