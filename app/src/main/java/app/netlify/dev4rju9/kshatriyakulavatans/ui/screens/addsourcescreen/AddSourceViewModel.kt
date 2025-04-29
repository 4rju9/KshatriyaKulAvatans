package app.netlify.dev4rju9.kshatriyakulavatans.ui.screens.addsourcescreen

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.content.Context
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.TIRAMISU
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Entity
import androidx.room.PrimaryKey
import app.netlify.dev4rju9.kshatriyakulavatans.data.repository.Repository
import app.netlify.dev4rju9.kshatriyakulavatans.others.Utility.isNetworkAvailable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddSourceViewModel @Inject constructor(
    val repo: Repository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddSourceUiState())
    val uiState: StateFlow<AddSourceUiState> = _uiState

    private val _selectedImages = MutableStateFlow<MutableList<Uri>>(mutableListOf())
    val selectedImages: StateFlow<List<Uri>> = _selectedImages.asStateFlow()

    private val _uploadProgress = MutableStateFlow(-1)
    val uploadProgress: StateFlow<Int> = _uploadProgress

    private val _isUploading = MutableStateFlow(false)
    val isUploading: StateFlow<Boolean> = _isUploading.asStateFlow()

    val imagePermission = if (SDK_INT >= TIRAMISU) {
        READ_MEDIA_IMAGES
    } else {
        READ_EXTERNAL_STORAGE
    }

    init {
        _isUploading.value = false
        _selectedImages.value = mutableListOf()
        _uploadProgress.value = -1
    }

    fun onImagesSelected(uris: List<Uri>) {
        _selectedImages.value = uris.toMutableList()
    }

    fun removeImage(uri: Uri) {
        _selectedImages.value = _selectedImages.value.toMutableList().apply {
            remove(uri)
        }
    }

    fun onTitleChange(value: String) {
        _uiState.update { it.copy(title = value) }
    }

    fun onSourceChange(value: String) {
        _uiState.update { it.copy(source = value) }
    }

    fun onDescriptionChange(value: String) {
        _uiState.update { it.copy(description = value) }
    }

    fun addTags(tags: List<String>) {
        val updatedTags = _uiState.value.tags.toMutableList()
        updatedTags.addAll(tags.filter { it.isNotEmpty() && !updatedTags.contains(it) })
        _uiState.update { it.copy(tags = updatedTags) }
    }

    fun removeTag(tag: String) {
        val updatedTags = _uiState.value.tags.toMutableList()
        updatedTags.remove(tag)
        _uiState.update { it.copy(tags = updatedTags) }
    }

    fun validateInput(): Boolean {
        val result = uiState.value.title.isNotBlank() &&
                uiState.value.description.isNotBlank() &&
                selectedImages.value.isNotEmpty() &&
                uiState.value.tags.isNotEmpty()
        Log.d("x4rju9", "validateInput: $result")
        return result
    }

    fun saveSource (context: Context, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {

            if (!isNetworkAvailable(context)) {
                onFailure(Exception("You're offline. Please try again once you're online."))
                return@launch
            }

            try {
                val imageUrls = mutableListOf<String>()
                val images = selectedImages.value
                _uploadProgress.value = 0
                _isUploading.value = true
                for (i in 0 until selectedImages.value.size) {
                    val url = repo.uploadImageToFirebase("${i+1}_${uiState.value.title.trim()}", selectedImages.value[i])
                    imageUrls.add(url)
                    _uploadProgress.value = ((i + 1) * 100) / images.size
                }

                _uiState.update { it.copy(
                    title = uiState.value.title.trim(),
                    description = uiState.value.description.trim(),
                    source = uiState.value.source.trim(),
                    imageUris = imageUrls,
                    researchedBy = repo.sharedPreferences.getString("username", "unknown")?: "unknown",
                    )
                }
                repo.saveSourceInFirebase(
                    uiState.value,
                    onSuccess = {
                        _uploadProgress.value = -1
                        reset()
                        onSuccess()
                    },
                    onError = {
                        _uploadProgress.value = -1
                        _isUploading.value = false
                        onFailure(it)
                    }
                )
            } catch (e: Exception) {
                _uploadProgress.value = -1
                _isUploading.value = false
                onFailure(e)
            }
        }
    }

    fun reset () {
        _uiState.update { AddSourceUiState() }
        _selectedImages.value = mutableListOf()
        _uploadProgress.value = -1
        _isUploading.value = false
    }

    fun refreshSources() {
        viewModelScope.launch {
            repo.refreshSources() // Syncs Firebase → Room
        }
    }

}

@Entity(tableName = "sources")
data class AddSourceUiState(
    val title: String = "",
    val description: String = "",
    val tags: List<String> = emptyList(),
    val source: String = "",
    val imageUris: List<String> = emptyList(),
    val researchedBy: String = "",
    @PrimaryKey
    val timestamp: Long = System.currentTimeMillis()
)