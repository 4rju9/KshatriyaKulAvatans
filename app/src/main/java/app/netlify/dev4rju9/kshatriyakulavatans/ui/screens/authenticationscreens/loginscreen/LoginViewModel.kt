package app.netlify.dev4rju9.kshatriyakulavatans.ui.screens.authenticationscreens.loginscreen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.netlify.dev4rju9.kshatriyakulavatans.data.models.User
import app.netlify.dev4rju9.kshatriyakulavatans.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repo: Repository
) : ViewModel() {

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var error by mutableStateOf<String?>(null)
    var isLoading by mutableStateOf(false)

    init {
        email = ""
        password = ""
        reset()
    }

    fun reset() {
        error = null
        isLoading = false
    }

    fun loginUser(onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            error = "Email and password are required."
            return
        }

        viewModelScope.launch {
            isLoading = true
            repo.loginUser(
                email.trim(),
                password,
                onSuccess = { user ->
                    saveUserInRoom(user)
                    email = ""
                    password = ""
                    reset()
                    onSuccess()
                },
                onError = { exception ->
                    isLoading = false
                    val message = exception.localizedMessage ?: "Login failed."
                    Log.e("x4rju9", "Login failed: $message", exception)
                    error = message
                    if (message.contains("Email not verified", ignoreCase = true)) {
                        repo.auth.signOut() // Sign out on failure (ensure fresh state)
                    }
                }
            )
        }
    }

    fun saveUserInRoom(user: User) {
        // Save the user details into shared preferences or room
        try {
            repo.sharedPreferences.edit().apply {
                putString("name", user.name)
                putString("username", user.username)
                putString("email", user.email)
                putBoolean("isAdmin", user.admin)
                apply()
            }
        } catch (e: Exception) { e.printStackTrace() }
        Log.d("x4rju9", "saveUserInRoom: $user")
        Log.d("x4rju9", "saveUserInRoom: ${repo.sharedPreferences.all}")
    }

    fun clearError () {
        error = ""
    }

    fun refreshSources() {
        viewModelScope.launch {
            repo.refreshSources() // Syncs Firebase → Room
        }
    }

    fun forgotPassword(onSuccess: () -> Unit) {
        if (email.isBlank()) {
            error = "Email is required."
            return
        }
        viewModelScope.launch {
            repo.forgotPassword(email, onSuccess)
        }
    }

}