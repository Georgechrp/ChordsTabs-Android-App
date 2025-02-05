package com.unipi.george.chordshub.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.unipi.george.chordshub.R
import com.unipi.george.chordshub.ui.theme.Blue40
import com.unipi.george.chordshub.ui.theme.checkedFilter
import com.unipi.george.chordshub.ui.theme.filterColor

@Composable
fun TopBar(
    fullName: String,
    painter: Painter,
    navController: NavController,
    isVisible: Boolean,
    onMenuClick: () -> Unit,
    selectedSong: SongLine?
) {
    if (isVisible) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Blue40)
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularImageView(painter = painter, onClick = { onMenuClick() })
            Spacer(modifier = Modifier.width(12.dp))

            if (currentRoute == "Home") {
                if (selectedSong == null) {
                    FilterRow()
                }
            }
        }
    }
}


@Composable
fun FilterButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(15.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) filterColor // Αν επιλεγεί
            else checkedFilter //Αν δεν είναι επιλεγμένο
        ),
        modifier = Modifier
            .height(30.dp)
            .wrapContentWidth()
    ) {
        Text(
            text,
            maxLines = 1,
            fontSize = 12.sp  //overflow = TextOverflow.Ellipsis
        )
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
fun FilterRow() {
    var selectedFilter by remember { mutableStateOf("All") }
    val scrollState = rememberScrollState()

    val filters = listOf("All", "Jazz", "Metal", "Classical", "Greek drill")
    val sortedFilters = filters.sortedBy { if (it == selectedFilter) 0 else 1 }

    LaunchedEffect(selectedFilter) {
        scrollState.animateScrollTo(0)
    }

    Row(
        modifier = Modifier
            .horizontalScroll(scrollState)
    ) {
        sortedFilters.forEach { filter ->
            FilterButton(
                text = filter,
                isSelected = filter == selectedFilter,
                onClick = { selectedFilter = filter }
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

