@file:OptIn(ExperimentalFoundationApi::class)
package app.netlify.dev4rju9.kshatriyakulavatans.ui.screens.mainscreen

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import app.netlify.dev4rju9.kshatriyakulavatans.data.models.VersionInfo
import app.netlify.dev4rju9.kshatriyakulavatans.others.Utility
import app.netlify.dev4rju9.kshatriyakulavatans.others.Utility.formatTimeAgo
import app.netlify.dev4rju9.kshatriyakulavatans.others.navigation.Screen
import app.netlify.dev4rju9.kshatriyakulavatans.ui.screens.addsourcescreen.AddSourceUiState
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(
    context: Context,
    navController: NavController,
    viewModel: MainScreenViewModel = hiltViewModel()
) {
    var showDialog by remember { mutableStateOf(false) }
    val name = (viewModel.name.value.split(" ").firstOrNull() ?: "There")
    var searchText by remember { mutableStateOf("") }
    val sources = viewModel.filteredSources.value
    val coroutineScope = rememberCoroutineScope()
    var searchJob by remember { mutableStateOf<Job?>(null) }
    var searchMode by remember { mutableStateOf(false) }
    val versionInfo = viewModel.versionState.value

    LaunchedEffect(name) {
        viewModel.updateName()
        Log.d("x4rju9", "MainScreen: $name")
    }

    LaunchedEffect(Unit) {
        viewModel.checkForUpdate(context)
    }

    versionInfo?.let {
        UpdateDialog(context, it) {
            viewModel.dismissDialog()
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    Column {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // Left side: Menu Icon + Greeting or CompactSearchBar
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { showDialog = true }) {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = "Menu",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(30.dp)
                    )
                }
                if (!searchMode) {

                    Spacer(modifier = Modifier.width(15.dp))

                    Text(
                        text = "Hey $name",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
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
                        viewModel.refreshSources()
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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp)
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
        ) {
            if (sources.isEmpty() && searchText.isNotEmpty()) {
                item {
                    Text(
                        text = "No sources found matching \"$searchText\"\nTry a different keyword or refresh the data.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Transparent, shape = RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    )
                }
            } else {
                items(sources) { source ->
                    SourceCard(
                        source,
                        viewModel.isAdmin.value,
                        onDelete = {
                            viewModel.deleteSource(source)
                            viewModel.refreshSources()
                        }
                    )
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Logout") },
            text = { Text("Do you want to logout?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.logOut()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                    showDialog = false
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("No")
                }
            }
        )
    }
}

@Composable
fun SourceCard(
    source: AddSourceUiState,
    isAdmin: Boolean = false,
    onDelete: () -> Unit = {},
    uriHandler: UriHandler = LocalUriHandler.current
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedImage by remember { mutableStateOf<String?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = Modifier
            .combinedClickable(
                onClick = { expanded = !expanded },
                onLongClick = {
                    if (isAdmin) {
                        showDialog = true
                    }
                }
            )
            .fillMaxWidth()
            .shadow(8.dp, shape = RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(15.dp))
            .padding(16.dp)
    ) {

        // Dialog for deleting the source
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(text = "Delete Source") },
                text = { Text("Are you sure you want to delete this source?") },
                confirmButton = {
                    TextButton(onClick = {
                        onDelete()
                        showDialog = false
                    }) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        Text(
            text = source.title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        clipboardManager.setText(AnnotatedString(source.title))
                    },
                    onTap = { expanded = !expanded }
                )
            }.padding(bottom = 10.dp)
        )

        Text(
            text = source.description,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            clipboardManager.setText(AnnotatedString(source.description))
                        },
                        onTap = { expanded = !expanded }
                    )
                }.padding(bottom = 10.dp)
        )

        ClickableTextWithLinks(
            text = "Source: ${source.source}",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = MaterialTheme.typography.bodySmall.fontSize * 1.1f)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Researched by: ${source.researcherName}",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.clickable { uriHandler.openUri("https://instagram.com/${source.researchedBy}") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Tags: ${source.tags.joinToString()}", style = MaterialTheme.typography.bodySmall)
        Text(
            text = formatTimeAgo(source.timestamp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
        )

        if (expanded && source.imageUris.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(source.imageUris) { uri ->
                    ZoomableImageThumbnail(uri) {
                        selectedImage = uri
                    }
                }
            }
        }

        selectedImage?.let {
            FullscreenZoomableImage(uri = it) {
                selectedImage = null
            }
        }
    }
}

@Composable
fun ClickableTextWithLinks(
    text: String,
    style: TextStyle = MaterialTheme.typography.bodySmall.copy(fontSize = MaterialTheme.typography.bodySmall.fontSize * 1.1f)
) {
    val uriHandler = LocalUriHandler.current
    val annotatedString = buildAnnotatedString {
        val regex = "(https?://[\\w\\-._~:/?@!$&'()*+,;=%]+)".toRegex(RegexOption.IGNORE_CASE)
        var lastIndex = 0
        regex.findAll(text).forEach { match ->
            val start = match.range.first
            val end = match.range.last + 1
            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onBackground)) {
                append(text.substring(lastIndex, start))
            }
            pushStringAnnotation(tag = "URL", annotation = match.value)
            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                append(match.value)
            }
            pop()
            lastIndex = end
        }
        append(text.substring(lastIndex))
    }

    ClickableText(
        text = annotatedString,
        style = style.copy(color = MaterialTheme.colorScheme.onBackground),
        onClick = { offset ->
            annotatedString.getStringAnnotations("URL", offset, offset)
                .firstOrNull()?.let { uriHandler.openUri(it.item) }
        }
    )
}

@Composable
fun ZoomableImageThumbnail(
    uri: String,
    onClick: () -> Unit
) {
    val painter = rememberAsyncImagePainter(model = uri)
    val state = painter.state

    Box(
        modifier = Modifier
            .height(300.dp)
            .width(200.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
    ) {
        Image(
            painter = painter,
            contentDescription = "Source Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
        )

        if (state is AsyncImagePainter.State.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.2f))
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxSize(0.4f),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun FullscreenZoomableImage(uri: String, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onDismiss() }
                .background(Color.Transparent)
        ) {
            var scale by remember { mutableFloatStateOf(1f) }
            var offset by remember { mutableStateOf(Offset(0f, 0f)) }

            val zoomState = rememberTransformableState { zoomChange, panChange, _ ->
                scale *= zoomChange
                // Restricting the pan range manually
                offset = Offset(
                    x = offset.x + panChange.x,
                    y = offset.y + panChange.y
                )

                // Manually clamp the offset values to avoid the image moving out of bounds
                val minOffset = Offset(-1000f, -1000f)
                val maxOffset = Offset(1000f, 1000f)

                offset = Offset(
                    x = offset.x.coerceIn(minOffset.x, maxOffset.x),
                    y = offset.y.coerceIn(minOffset.y, maxOffset.y)
                )
            }

            AsyncImage(
                model = uri,
                contentDescription = "Source Image",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = scale.coerceIn(1f, 4f),
                        scaleY = scale.coerceIn(1f, 4f),
                        translationX = offset.x,
                        translationY = offset.y
                    )
                    .transformable(zoomState)
            )
        }
    }
}

@Composable
fun CompactSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onClose: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .height(36.dp)
            .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.background, RoundedCornerShape(16.dp))
            .padding(horizontal = 8.dp, vertical = 0.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        var isFocused by remember { mutableStateOf(false) }

        // Layered box to show placeholder underneath
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(end = 4.dp)
                    .size(16.dp)
            )

            Box(modifier = Modifier.weight(1f)) {
                if (value.isEmpty() && !isFocused) {
                    Text(
                        text = "Search...",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { isFocused = it.isFocused }
                )
            }

            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close Icon",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .size(16.dp)
                    .clickable { onClose() }
            )
        }
    }
}

@Composable
fun UpdateDialog(
    context: Context,
    versionInfo: VersionInfo,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        confirmButton = {
            TextButton(onClick = {
                Utility.downloadApk(context, versionInfo.apkUrl)
                onDismiss()
            }) {
                Text(text = "Update", color = MaterialTheme.colorScheme.primary)
            }
        },
        title = { Text("Product Update!") },
        text = {
            Text(text = "A new version is available.\n\n" +
                    "Current: ${versionInfo.currentName}  Latest: ${versionInfo.latestName}\n\n" +
                    versionInfo.releaseNotes, color = MaterialTheme.colorScheme.onBackground)
        },
        dismissButton = null
    )
}