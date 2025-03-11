package com.unipi.george.chordshub.utils

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.net.URLEncoder

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import kotlinx.coroutines.launch
/**
 * Αναζήτηση στο Wikipedia για να βρούμε το πιο σχετικό όνομα σελίδας
 */
suspend fun searchWikipediaPage(artistName: String): String? {
    return withContext(Dispatchers.IO) {
        try {
            val encodedQuery = URLEncoder.encode(artistName, "UTF-8")
            val url = "https://en.wikipedia.org/w/api.php?action=query&list=search&srsearch=$encodedQuery&format=json"
            val json = JSONObject(URL(url).readText())

            val searchResults = json.getJSONObject("query").getJSONArray("search")
            if (searchResults.length() > 0) {
                searchResults.getJSONObject(0).getString("title") // Παίρνουμε το πρώτο αποτέλεσμα
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("WikiSearch", "Σφάλμα αναζήτησης", e)
            null
        }
    }
}

/**
 * Παίρνει την εικόνα και την περίληψη από τη Wikipedia για τον καλλιτέχνη
 */
suspend fun fetchWikipediaData(pageTitle: String): Pair<String?, String?>? {
    return withContext(Dispatchers.IO) {
        try {
            val encodedTitle = URLEncoder.encode(pageTitle, "UTF-8")
            val url = "https://en.wikipedia.org/api/rest_v1/page/summary/$encodedTitle"
            val json = JSONObject(URL(url).readText())

            val imageUrl = json.optJSONObject("thumbnail")?.getString("source")
            val summary = json.optString("extract") // Η πρώτη παράγραφος

            Pair(imageUrl, summary)
        } catch (e: Exception) {
            Log.e("WikiData", "Σφάλμα φόρτωσης δεδομένων", e)
            null
        }
    }
}


@Composable
fun ArtistInfo(artistName: String) {
    var imageUrl by remember { mutableStateOf<String?>(null) }
    var summary by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(artistName) {
        coroutineScope.launch {
            val bestMatchTitle = searchWikipediaPage(artistName)
            if (bestMatchTitle != null) {
                val data = fetchWikipediaData(bestMatchTitle)
                imageUrl = data?.first
                summary = data?.second
            } else {
                summary = "Δεν βρέθηκαν πληροφορίες."
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        imageUrl?.let {
            Image(
                painter = rememberImagePainter(it),
                contentDescription = "Artist Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        summary?.let {
            Text(text = it, style = MaterialTheme.typography.bodyLarge)
        } ?: Text(text = "Φόρτωση πληροφοριών...", modifier = Modifier.padding(16.dp))
    }
}