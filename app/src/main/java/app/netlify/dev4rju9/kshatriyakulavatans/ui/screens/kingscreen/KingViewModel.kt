package app.netlify.dev4rju9.kshatriyakulavatans.ui.screens.kingscreen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.netlify.dev4rju9.kshatriyakulavatans.data.models.King
import app.netlify.dev4rju9.kshatriyakulavatans.data.remote.retrofit.CloudFlare
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KingViewModel @Inject constructor(
    private val api: CloudFlare
) : ViewModel() {

    private val _kings = mutableStateOf<KingUiState>(KingUiState.Loading)
    val filteredUIState: State<KingUiState> = _kings

    private val _searchQuery = mutableStateOf("")
    private val _allKings = mutableStateOf<List<King>>(emptyList())

    init {
        fetchKings()
    }

    fun fetchKings() {
        viewModelScope.launch {
            _kings.value = KingUiState.Loading
            try {
                val response = api.getKings()
                if (response.statusCode == 200) {
                    val kings = response.body
                    _allKings.value = kings
                    applySearchFilter(_searchQuery.value)
                } else {
                    _kings.value = KingUiState.Error("API Error: ${response.statusCode}")
                }
            } catch (e: Exception) {
                _kings.value = KingUiState.Error("Network Error: ${e.message}")
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        applySearchFilter(query)
    }

    private fun applySearchFilter(query: String) {
        val filtered = if (query.isBlank()) {
            _allKings.value
        } else {
            val cleanQuery = query.trim().lowercase()
            _allKings.value.filter { king ->
                king.name.contains(cleanQuery, ignoreCase = true) ||
                        king.mainContent.contains(cleanQuery, ignoreCase = true) ||
                        king.sections.any { section ->
                            section.title.contains(cleanQuery, ignoreCase = true) ||
                            section.text.contains(cleanQuery, ignoreCase = true)
                        }
            }
        }
        _kings.value = if (filtered.isEmpty()) {
            KingUiState.Success(emptyList())
        } else {
            KingUiState.Success(filtered)
        }
    }
}

sealed class KingUiState {
    object Loading : KingUiState()
    data class Success(val kings: List<King>) : KingUiState()
    data class Error(val message: String) : KingUiState()
}