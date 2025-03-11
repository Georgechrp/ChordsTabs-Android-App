package com.unipi.george.chordshub.utils

import android.graphics.Bitmap
import android.graphics.Color
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.unipi.george.chordshub.R
import com.unipi.george.chordshub.viewmodels.main.SearchViewModel


fun generateQRCode(content: String): Bitmap? {
    return try {
        val qrCodeWriter = QRCodeWriter()
        val bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 512, 512)
        val bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.RGB_565)
        for (x in 0 until 512) {
            for (y in 0 until 512) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        bitmap
    } catch (e: Exception) {
        null
    }
}


@Composable
fun QRCodeDialog(showDialog: MutableState<Boolean>, songId: String) {
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            confirmButton = {
                Button(onClick = { showDialog.value = false }) {
                    Text("Κλείσιμο")
                }
            },
            title = { Text("Κοινοποίηση μέσω QR") },
            text = {
                val qrBitmap = generateQRCode(songId)
                qrBitmap?.let {
                    Image(bitmap = it.asImageBitmap(), contentDescription = "QR Code")
                } ?: Text("Σφάλμα δημιουργίας QR")
            }
        )
    }
}


@Composable
fun QRCodeButton(showQRCodeDialog: MutableState<Boolean>) {
    IconButton(onClick = { showQRCodeDialog.value = true }) {
        Icon(
            painter = painterResource(id = R.drawable.generateqrcode),
            contentDescription = "Share via QR"
        )
    }
}

@Composable
fun QRCodeScannerButton(viewModel: SearchViewModel) {
    val scanLauncher = rememberLauncherForActivityResult(ScanContract()) { result ->
        result.contents?.let { scannedId ->
            viewModel.selectSong(scannedId)
        }
    }

    Box(
        modifier = Modifier
            .size(30.dp)
            .clickable {
                val options = ScanOptions()
                options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                options.setPrompt("Σκάναρε το QR Code")
                options.setCameraId(0)
                options.setOrientationLocked(true)
                options.setBeepEnabled(true)
                options.setBarcodeImageEnabled(true)
                scanLauncher.launch(options)
            }
    ) {
        Icon(
            painter = painterResource(id = R.drawable.scanner),
            contentDescription = "Σκανάρισμα QR",
            modifier = Modifier.fillMaxSize()
        )
    }
}
