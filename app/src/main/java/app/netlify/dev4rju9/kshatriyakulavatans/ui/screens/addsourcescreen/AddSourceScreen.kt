package app.netlify.dev4rju9.kshatriyakulavatans.ui.screens.addsourcescreen

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import app.netlify.dev4rju9.kshatriyakulavatans.ui.screens.authenticationscreens.registrationscreen.TextField
import coil.compose.AsyncImage
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSourceScreen(
    navController: NavController,
    viewModel: AddSourceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var tagInput by remember { mutableStateOf("") }
    val uploadProgress by viewModel.uploadProgress.collectAsState()
    val isUploading by viewModel.isUploading.collectAsState()

    val selectedImages by viewModel.selectedImages.collectAsState()
    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(10),
        onResult = { uris ->
            if (uris.isNotEmpty()) viewModel.onImagesSelected(uris)
        }
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                multiplePhotoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            } else {
                Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    )

    LaunchedEffect(navController.currentBackStackEntry) {
        viewModel.reset()
    }

    BackHandler {
        navController.popBackStack()
        viewModel.reset()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Source") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                        viewModel.reset()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (viewModel.validateInput() && !isUploading) {
                        viewModel.saveSource(
                            context,
                            onSuccess = {
                                viewModel.reset()
                                Toast.makeText(context, "Source added", Toast.LENGTH_SHORT).show()
                                viewModel.refreshSources()
                                navController.popBackStack()
                            },
                            onFailure = { e ->
                                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                                Log.e("x4rju9", "AddSourceScreen Error: ${e.message}", e)
                            }
                        )
                    } else {
                        val error = if (isUploading) "Uploading..." else "Please fill all fields"
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                    }
                },
                containerColor = if (isUploading) Color.Gray else MaterialTheme.colorScheme.primary,
                shape = CircleShape,
                modifier = Modifier
                    .padding(20.dp)
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Save",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {

                if (uploadProgress > -1) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Uploading...",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = { uploadProgress / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(50)),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "$uploadProgress%",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                if (selectedImages.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(selectedImages, key = { it }) { uri ->
                            Box(
                                modifier = Modifier
                                    .height(300.dp)
                            ) {
                                AsyncImage(
                                    model = uri,
                                    contentDescription = "Selected image",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(8.dp))
                                )

                                IconButton(
                                    onClick = { viewModel.removeImage(uri) },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .offset(x = (-8).dp, y = (8).dp)
                                        .background(
                                            color = Color.Black.copy(alpha = 0.6f),
                                            shape = CircleShape
                                        )
                                        .size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove image",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        OutlinedButton(
                            onClick = { permissionLauncher.launch(viewModel.imagePermission) },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Images",
                                tint = MaterialTheme.colorScheme.onBackground,
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Upload Images",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 15.sp
                            )
                        }
                    }
                }

                TextField(
                    value = uiState.title,
                    onValueChange = { viewModel.onTitleChange(it) },
                    label = "Title",
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = uiState.description,
                    onValueChange = { viewModel.onDescriptionChange(it) },
                    label = "Description",
                    singleLine = false,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = uiState.source,
                    onValueChange = { viewModel.onSourceChange(it) },
                    label = "Source:",
                    placeholder = { Text("Enter Source Name/Url") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Custom Tag Input Container
                TextField(
                    value = tagInput,
                    onValueChange = { it ->
                        tagInput = it
                        if (it.contains(",")) {
                            val tags = it.split(",")
                                .map { tag -> tag.trim()
                                    .replaceFirstChar {
                                        if (it.isLowerCase()) it.titlecase(Locale.ROOT)
                                        else it.toString()
                                    }
                                }
                                .filter { tag -> tag.isNotEmpty() }
                            viewModel.addTags(tags)
                            tagInput = ""
                        }
                    },
                    label = "Tags",
                    placeholder = { Text("Enter tags separated by commas.") },
                    trailingIcon = {
                        if (tagInput.isNotBlank()) {
                            IconButton(onClick = {
                                val tags = tagInput.split(",")
                                    .map { tag -> tag.trim() }
                                    .filter { tag -> tag.isNotEmpty() }
                                viewModel.addTags(tags)
                                tagInput = ""
                            }) {
                                Icon(Icons.Default.Check, contentDescription = "Add tag")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                if (uiState.tags.isNotEmpty()) {
                    val listState = rememberLazyListState()

                    LazyRow(
                        state = listState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.tags) { tag ->
                            AssistChip(
                                onClick = { viewModel.removeTag(tag) },
                                label = { Text(tag) }
                            )
                        }
                    }
                    LaunchedEffect(uiState.tags.size) {
                        if (uiState.tags.isNotEmpty()) {
                            listState.animateScrollToItem(uiState.tags.size - 1)
                        }
                    }
                }
            }
        }
    )
}