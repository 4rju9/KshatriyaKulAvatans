package app.netlify.dev4rju9.kshatriyakulavatans.ui.screens.profilescreen

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import app.netlify.dev4rju9.kshatriyakulavatans.ui.screens.authenticationscreens.registrationscreen.RegisterViewModel
import app.netlify.dev4rju9.kshatriyakulavatans.ui.screens.authenticationscreens.registrationscreen.TextField
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequesterUsername = remember { FocusRequester() }
    val scaffoldState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val fieldModifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 6.dp)

    // Load user data when screen is shown
    LaunchedEffect(Unit) {
        viewModel.loadUserData()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()
            )
    ) {

        Text("Profile", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = viewModel.name,
            onValueChange = { viewModel.name = it },
            label = "Full Name",
            modifier = fieldModifier,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusRequesterUsername.requestFocus() })
        )

        TextField(
            value = viewModel.username,
            onValueChange = { viewModel.username = it.replace("@", "") },
            label = "Instagram Username",
            modifier = fieldModifier.focusRequester(focusRequesterUsername),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
        )

        if (viewModel.isAdmin.value) {
            OutlinedTextField(
                value = if (
                    viewModel.originalUsername.contains(
                        "aruu_gangwar",
                        true
                    )
                ) "Developer" else "Administrator",
                onValueChange = {},
                label = { Text("Role", color = MaterialTheme.colorScheme.onBackground) },
                singleLine = true,
                modifier = fieldModifier,
                textStyle = TextStyle(fontSize = 12.sp),
                shape = RoundedCornerShape(20.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                keyboardController?.hide()
                viewModel.updateUserData(
                    onSuccess = {
                        Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    },
                    onFailure = {
                        Toast.makeText(context, it.message ?: "Update failed", Toast.LENGTH_SHORT).show()
                    }
                )
            },
            enabled = viewModel.hasChanges() && !viewModel.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (viewModel.isLoading) "Updating..." else "Update")
        }

        viewModel.error?.let {
            if (viewModel.error?.isNotEmpty() == true) {
                coroutineScope.launch {
                    scaffoldState.showSnackbar(
                        message = it,
                        withDismissAction = true,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

}