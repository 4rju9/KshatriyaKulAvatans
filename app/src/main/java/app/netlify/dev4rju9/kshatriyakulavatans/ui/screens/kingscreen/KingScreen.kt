package app.netlify.dev4rju9.kshatriyakulavatans.ui.screens.kingscreen

import ShimmerKingCard
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import app.netlify.dev4rju9.kshatriyakulavatans.data.models.King
import app.netlify.dev4rju9.kshatriyakulavatans.ui.screens.mainscreen.CompactSearchBar
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import shimmerEffect
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KingScreen(viewModel: KingViewModel = hiltViewModel()) {

    val uiState by viewModel.filteredUIState
    var searchMode by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    var searchJob by remember { mutableStateOf<Job?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            if (!searchMode) {
                Text(
                    text = "Kurmi Kings",
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }

            if (searchMode) {
                CompactSearchBar(
                    value = searchText,
                    onValueChange = {
                        searchText = it
                        searchJob?.cancel()
                        searchJob = coroutineScope.launch {
                            delay(300)
                            viewModel.updateSearchQuery(it)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    onClose = {
                        searchText = ""
                        searchMode = false
                        viewModel.updateSearchQuery("")
                    }
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Search Toggle Button

                if (!searchMode) {
                    Button(
                        onClick = { searchMode = !searchMode },
                        modifier = Modifier
                            .height(36.dp)
                            .align(Alignment.CenterVertically),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Icon",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                // Refresh Button
                Button(
                    onClick = {
                        viewModel.fetchKings()
                        searchText = ""
                        searchMode = false
                        viewModel.updateSearchQuery("")
                    },
                    modifier = Modifier
                        .height(36.dp)
                        .align(Alignment.CenterVertically),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh Icon",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
        }

        when (uiState) {
            is KingUiState.Loading -> {
                LazyColumn {
                    items(5) {
                        ShimmerKingCard()
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
            is KingUiState.Success -> {
                val kings = (uiState as KingUiState.Success).kings
                if (kings.isEmpty()) {
                    Text(
                        text = "No Kings Found.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Transparent, shape = RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    )
                } else {
                    LazyColumn {
                        items(kings) { king ->
                            KingCard(king)
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }
            is KingUiState.Error -> {
                val message = (uiState as KingUiState.Error).message
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Transparent, shape = RoundedCornerShape(12.dp))
                        .padding(16.dp)
                )
            }
        }
    }

}

@Composable
fun KingCard(king: King) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { expanded = !expanded },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {

        val painter = rememberAsyncImagePainter(model = king.image)
        val state = painter.state

        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = king.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Image(
                    painter = painter,
                    contentDescription = king.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
                if (state is AsyncImagePainter.State.Loading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .height(200.dp)
                            .shimmerEffect()
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = king.mainContent,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                king.sections.forEach { (title, content) ->
                    Text(
                        text = title.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
                        },
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = content,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}