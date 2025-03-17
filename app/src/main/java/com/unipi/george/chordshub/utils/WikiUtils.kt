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
import java.net.HttpURLConnection


@Composable
fun ArtistInfo(artistName: String) {
    var imageUrl by remember { mutableStateOf<String?>(null) }
    var summary by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(artistName) {
        coroutineScope.launch {
            val bestMatch = getWikipediaPageTitle(artistName) // ✅ Επιστρέφει και το lang τώρα
            if (bestMatch != null) {
                val (bestMatchTitle, lang) = bestMatch
                val data = bestMatchTitle?.let { fetchWikipediaData(it, lang) }
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

// ✅ Παίρνει την εικόνα και την περίληψη από τη Wikipedia με βάση τη γλώσσα
suspend fun fetchWikipediaData(pageTitle: String, lang: String): Pair<String?, String?>? {
    return withContext(Dispatchers.IO) {
        try {
            val encodedTitle = URLEncoder.encode(pageTitle, "UTF-8")
            val url = "https://$lang.wikipedia.org/api/rest_v1/page/summary/$encodedTitle"
            val json = JSONObject(URL(url).readText())

            val imageUrl = json.optJSONObject("thumbnail")?.getString("source")
            val summary = json.optString("extract")

            Pair(imageUrl, summary)
        } catch (e: Exception) {
            Log.e("WikiData", "Σφάλμα φόρτωσης δεδομένων", e)
            null
        }
    }
}

// ✅ Ελέγχει αν μια Wikipedia σελίδα υπάρχει σε συγκεκριμένη γλώσσα
suspend fun checkWikipediaPageExists(title: String, lang: String): Boolean {
    return withContext(Dispatchers.IO) {
        try {
            val url = "https://$lang.wikipedia.org/wiki/${URLEncoder.encode(title, "UTF-8")}"
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "HEAD" // Δεν κατεβάζουμε όλο το HTML, απλά ελέγχουμε αν υπάρχει
            connection.responseCode == 200
        } catch (e: Exception) {
            false
        }
    }
}


suspend fun getWikipediaPageTitle(artistName: String): Pair<String?, String>? {
    return withContext(Dispatchers.IO) {
        val formattedName = artistName.replace(" ", "_")

        // 🔹 Επιλογή γλώσσας (Αν το όνομα είναι ελληνικό, ψάχνουμε στην ελληνική Wikipedia)
        val isGreek = artistName.any { it in 'Α'..'Ω' || it in 'α'..'ω' }
        val lang = if (isGreek) "el" else "en"

        Log.d("WikipediaSearch", "Ψάχνουμε για: $formattedName στη γλώσσα: $lang")

        // 🔹 1. Δοκιμάζουμε πρώτα με "(band)", "(musician)", "(singer)"
        val possibleVariants = listOf("${formattedName}_(band)", "${formattedName}_(musician)", "${formattedName}_(singer)")
        for (variant in possibleVariants) {
            if (checkWikipediaPageExists(variant, lang)) {
                Log.d("WikipediaSearch", "Βρέθηκε σελίδα: $variant")
                return@withContext Pair(variant, lang)
            }
        }

        // 🔹 2. Αν δεν βρήκε με τις παρενθέσεις, δοκιμάζουμε το απλό όνομα
        if (checkWikipediaPageExists(formattedName, lang)) {
            Log.d("WikipediaSearch", "Βρέθηκε απλό όνομα: $formattedName")
            return@withContext Pair(formattedName, lang)
        }

        // 🔹 3. Αν δεν βρούμε τίποτα, κάνουμε αναζήτηση στην Wikipedia
        val searchResult = searchWikipediaPage(artistName, lang)
        if (searchResult != null) {
            Log.d("WikipediaSearch", "Αποτέλεσμα αναζήτησης: ${searchResult.first}")
            return@withContext searchResult
        }

        // 🔹 4. Τελευταία λύση: Επιστρέφουμε το default formatted όνομα
        Log.w("WikipediaSearch", "Δεν βρέθηκε τίποτα, επιστρέφουμε default: $formattedName")
        return@withContext Pair(formattedName, lang)
    }
}


// ✅ Αναζητά το πιο σχετικό αποτέλεσμα στη Wikipedia σε Αγγλικά ή Ελληνικά
suspend fun searchWikipediaPage(artistName: String, lang: String): Pair<String?, String>? {
    return withContext(Dispatchers.IO) {
        try {
            val encodedQuery = URLEncoder.encode(artistName, "UTF-8")
            val url = "https://$lang.wikipedia.org/w/api.php?action=query&list=search&srsearch=$encodedQuery&format=json"
            val json = JSONObject(URL(url).readText())

            val searchResults = json.getJSONObject("query").getJSONArray("search")
            if (searchResults.length() > 0) {
                return@withContext Pair(searchResults.getJSONObject(0).getString("title"), lang) // Παίρνουμε το πρώτο αποτέλεσμα
            }
        } catch (e: Exception) {
            Log.e("WikiSearch", "Σφάλμα αναζήτησης", e)
        }
        null
    }
}