package com.unipi.george.chordshub.components


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.unipi.george.chordshub.R
import com.unipi.george.chordshub.ui.theme.checkedFilter
import com.unipi.george.chordshub.ui.theme.filterColor
import com.unipi.george.chordshub.viewmodels.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAppTopBar(
    mainViewModel: MainViewModel,
    onMenuClick: () -> Unit,
    content: @Composable RowScope.() -> Unit
) {
    val profileImageUrl by mainViewModel.profileImageUrl.collectAsState()

    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
            ) {
                CircularImageViewSmall(
                    imageUrl = profileImageUrl,
                    onClick = onMenuClick
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(modifier = Modifier.weight(1f)) {
                    this@Row.content()
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
        )

    )
}



@Composable
fun FilterRow(selectedFilter: String, onFilterChange: (String) -> Unit) {
    val scrollState = rememberScrollState()
    val filters = listOf("All", "Jazz", "Metal", "Classical", "Britpop", "Pop", "Rock")

    Row(modifier = Modifier.horizontalScroll(scrollState)) {
        filters.forEach { filter ->
            FilterButton(
                text = filter,
                isSelected = filter == selectedFilter,
                onClick = { onFilterChange(filter) }
            )
            //Spacer(modifier = Modifier.width(2.dp))
        }
    }
}


@Composable
fun FilterButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(15.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) checkedFilter
            else filterColor
        ),
        modifier = Modifier
            .height(30.dp)
            .padding(horizontal = 4.dp)
    ) {
        Text(
            text,
            maxLines = 1,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}


@Composable
fun CircularImageViewSmall(imageUrl: String?, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(30.dp)
            .clip(CircleShape)
            .border(2.dp, Color.Gray, CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (imageUrl != null) {
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = stringResource(R.string.circular_image_description),
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.user_icon),
                contentDescription = stringResource(R.string.circular_image_description),
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
