package app.netlify.dev4rju9.kshatriyakulavatans.ui.screens.administratorscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.netlify.dev4rju9.kshatriyakulavatans.data.models.User
import app.netlify.dev4rju9.kshatriyakulavatans.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val repo: Repository
) : ViewModel() {

    private val _users = MutableStateFlow<List<User>>(emptyList())
    private val _searchQuery = MutableStateFlow("")

    val filteredUsers: StateFlow<List<User>> = combine(_users, _searchQuery) { users, query ->
        if (query.isBlank()) {
            users
        } else {
            users.filter { user ->
                user.name.contains(query, ignoreCase = true) ||
                        user.username.contains(query, ignoreCase = true) ||
                        user.email.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    init {
        viewModelScope.launch {
            repo.getUsers().collectLatest { sourceList ->
                _users.value = sourceList
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun refreshSources() {
        viewModelScope.launch {
            repo.fetchAndCacheUsers()
        }
    }

    fun updateRole(email: String, isAdmin: Boolean, onError: (Exception) -> Unit) {
        viewModelScope.launch {
            repo.toggleAdminStatus(email, isAdmin, onError)
        }
    }
}