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

    init {
        email = ""
        password = ""
        error = null
        isLoading = false
        name = ""
        username = ""
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