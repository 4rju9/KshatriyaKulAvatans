package app.netlify.dev4rju9.kshatriyakulavatans.ui.screens.authenticationscreens.registrationscreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.netlify.dev4rju9.kshatriyakulavatans.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repo: Repository
) : ViewModel() {

    var name by mutableStateOf("")
    var username by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var error by mutableStateOf<String?>(null)
    var isLoading by mutableStateOf(false)
    var isAdmin = mutableStateOf(repo.sharedPreferences.getBoolean("isAdmin", false))

    var originalName by mutableStateOf("")
    var originalUsername by mutableStateOf("")

    init {
        email = ""
        password = ""
        error = null
        isLoading = false
        name = ""
        username = ""
    }

    fun loadUserData() {
        viewModelScope.launch {
            try {
                val user = repo.getCurrentUserData()
                name = user.name
                username = user.username

                originalName = user.name
                originalUsername = user.username
            } catch (e: Exception) {
                error = e.message
            }
        }
    }

    fun hasChanges(): Boolean {
        return name != originalName || username != originalUsername
    }

    fun updateUserData(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {

        if (!hasChanges()) return

        viewModelScope.launch {
            isLoading = true
            repo.updateUserProfile(
                name = name,
                username = username,
                onSuccess = {
                    originalName = name
                    originalUsername = username
                    isLoading = false
                    onSuccess()
                },
                onError = {
                    isLoading = false
                    onFailure(it)
                }
            )
        }
    }

    fun reset() {
        error = null
        isLoading = false
    }

    fun onSignUp(onSuccess: () -> Unit) {
        if (name.isBlank() || username.isBlank() || email.isBlank() || password.isBlank()) {
            error = "All fields are required."
            return
        }

        viewModelScope.launch {
            isLoading = true
            repo.onSignUp(
                name,
                username,
                email,
                password,
                onSuccess = {
                    reset()
                    onSuccess()
                },
                onError = {
                    isLoading = false
                    error = it
                }
            )
        }
    }
}