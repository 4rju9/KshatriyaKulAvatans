package app.netlify.dev4rju9.kshatriyakulavatans.ui.screens.mainscreen

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.netlify.dev4rju9.kshatriyakulavatans.data.models.VersionInfo
import app.netlify.dev4rju9.kshatriyakulavatans.data.repository.Repository
import app.netlify.dev4rju9.kshatriyakulavatans.ui.screens.addsourcescreen.AddSourceUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    val repo: Repository
): ViewModel() {

    val user by lazy { repo.auth.currentUser }
    var name = mutableStateOf(repo.sharedPreferences.getString("name", "There")?: "There")
    var isAdmin = mutableStateOf(repo.sharedPreferences.getBoolean("isAdmin", false))

    private val _sources = mutableStateOf<List<AddSourceUiState>>(emptyList())
    val sources: State<List<AddSourceUiState>> = _sources

    private val _searchQuery = mutableStateOf("")

    private val _versionState = mutableStateOf<VersionInfo?>(null)
    val versionState: State<VersionInfo?> = _versionState

    init {
        viewModelScope.launch {
            repo.getSources().collectLatest { sourceList ->
                _sources.value = sourceList
            }
        }
    }

    fun updateName () {
        name.value = repo.sharedPreferences.getString("name", "There")?: "There"
    }

    fun refreshSources() {
        viewModelScope.launch {
            repo.refreshSources() // Syncs Firebase → Room
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    val filteredSources: State<List<AddSourceUiState>> = derivedStateOf {
        if (_searchQuery.value.isBlank() || _searchQuery.value.isEmpty()) {
            sources.value
        } else {
            val query = _searchQuery.value.trim().lowercase()
            sources.value.filter { source ->
                source.title.contains(query, ignoreCase = true) ||
                        source.description.contains(query, ignoreCase = true) ||
                        source.tags.any { it.contains(query, ignoreCase = true) } ||
                        source.source.contains(query, ignoreCase = true)
            }
        }
    }

    fun checkForUpdate(context: Context) {
        viewModelScope.launch {
            repo.fetchVersionInfo(context).onSuccess {
                if (it.latestCode > it.currentCode) {
                    _versionState.value = it
                }
            }.onFailure {
                Log.d("x4rju9", "checkForUpdate: ${it.message}")
            }
        }
    }

    fun dismissDialog() {
        _versionState.value = null
    }

    fun deleteSource (source: AddSourceUiState) {
        viewModelScope.launch {
            repo.deleteSourceAndImages(source)
        }
    }

    fun logOut () {
        viewModelScope.launch {
            repo.signOut()
        }
    }

}