package com.unipi.george.chordshub.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import com.unipi.george.chordshub.viewmodels.main.HomeViewModel
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import com.unipi.george.chordshub.models.song.SongLine

/*
*   3 functions about print Cards-Songs and clickable chords
*/

@Composable
fun CardsView(
    songList: List<Pair<String?, String>>,
    homeViewModel: HomeViewModel,
    selectedTitle: MutableState<String?>,
    columns: Int = 2,
    cardHeight: Dp? = null,
    cardElevation: Dp = 8.dp,
    cardPadding: Dp = 16.dp,
    gridPadding: Dp = 16.dp,
    fontSize: TextUnit = 16.sp,
    onSongClick: ((songId: String) -> Unit)? = null
) {
    val colors = listOf(
        Color(0xFFEF9A9A),
        Color(0xFF90CAF9),
        Color(0xFFA5D6A7),
        Color(0xFFFFF59D),
        Color(0xFFCE93D8)
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = Modifier
            .fillMaxSize()
            .padding(gridPadding),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(songList) { index, (title, songId) ->
            val backgroundColor = colors[index % colors.size]
            if (title != null) {
                SongCard(
                    title = title,
                    backgroundColor = backgroundColor,
                    cardHeight = cardHeight,
                    cardElevation = cardElevation,
                    cardPadding = cardPadding,
                    fontSize = fontSize,
                    onClick = {
                        Log.d("CardsView", "Selected song ID: $songId")
                        homeViewModel.selectSong(songId)
                        selectedTitle.value = title
                        onSongClick?.invoke(songId)
                    }
                )
            }
        }
    }
}

@Composable
fun SongCard(
    title: String,
    backgroundColor: Color,
    cardHeight: Dp? = null,
    cardElevation: Dp = 8.dp,
    cardPadding: Dp = 16.dp,
    fontSize: TextUnit = 16.sp,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (cardHeight != null) Modifier.height(cardHeight) else Modifier.aspectRatio(1f))
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = cardElevation),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(cardPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = fontSize,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black
            )
        }
    }
}


@Composable
fun ArtistCard(
    name: String,
    backgroundColor: Color,
    cardWidth: Dp = 180.dp,
    cardHeight: Dp = 100.dp,
    cardElevation: Dp = 8.dp,
    cardPadding: Dp = 16.dp,
    fontSize: TextUnit = 16.sp,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(cardWidth)
            .height(cardHeight)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = cardElevation),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(cardPadding),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name,
                fontSize = fontSize,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black
            )
        }
    }
}



@Composable
fun HorizontalArtistCardsView(
    artists: List<String>,
    homeViewModel: HomeViewModel,
    selectedTitle: MutableState<String?>,
    cardPadding: Dp = 16.dp,
    fontSize: TextUnit = 14.sp, // λίγο πιο μικρό
    onArtistClick: (artist: String) -> Unit
) {
    val colors = listOf(
        Color(0xFFEF9A9A),
        Color(0xFF90CAF9),
        Color(0xFFA5D6A7),
        Color(0xFFFFF59D),
        Color(0xFFCE93D8)
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(12.dp)) // απόσταση από πάνω

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = cardPadding),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(artists) { index, artist ->
                val backgroundColor = colors[index % colors.size]
                ArtistCard(
                    name = artist,
                    backgroundColor = backgroundColor,
                    cardWidth = 120.dp,
                    cardHeight = 60.dp,
                    fontSize = fontSize,
                    onClick = {
                        homeViewModel.fetchFilteredSongs(artist)
                        selectedTitle.value = artist
                        onArtistClick(artist)
                    }
                )
            }
        }
    }
}



@Composable
fun ArtistGridView(
    artistList: List<String>,
    homeViewModel: HomeViewModel,
    selectedTitle: MutableState<String?>,
    columns: Int = 2,
    cardHeight: Dp = 80.dp,
    fontSize: TextUnit = 16.sp,
    onArtistClick: ((artist: String) -> Unit)? = null
) {
    val colors = listOf(
        Color(0xFFEF9A9A),
        Color(0xFF90CAF9),
        Color(0xFFA5D6A7),
        Color(0xFFFFF59D),
        Color(0xFFCE93D8)
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(artistList) { index, artist ->
            val backgroundColor = colors[index % colors.size]
            ArtistCard(
                name = artist,
                backgroundColor = backgroundColor,
                fontSize = fontSize,
                cardHeight = cardHeight,
                onClick = {
                    homeViewModel.fetchFilteredSongs(artist)
                    selectedTitle.value = artist
                    onArtistClick?.invoke(artist)
                }
            )
        }
    }
}
