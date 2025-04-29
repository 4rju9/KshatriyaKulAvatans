package app.netlify.dev4rju9.kshatriyakulavatans.ui.screens.authenticationscreens.loginscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import app.netlify.dev4rju9.kshatriyakulavatans.R
import app.netlify.dev4rju9.kshatriyakulavatans.others.Screen
import app.netlify.dev4rju9.kshatriyakulavatans.ui.screens.authenticationscreens.registrationscreen.TextField
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    navController: NavController,
    onLoginSuccess: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val scaffoldState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(navController.currentBackStackEntry, Unit) {
        viewModel.reset()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = scaffoldState) }
    )
    { innerPadding ->
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val fieldModifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)

            Image(
                painter = painterResource(id = R.drawable.logo),
                modifier = Modifier
                    .fillMaxWidth(0.40f)
                    .clip(CircleShape),
                contentDescription = "App Logo",
                alignment = Alignment.TopCenter
            )

            Spacer(modifier = Modifier.height(40.dp))

            val loginMessage = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 25.sp
                    )
                ) {
                    append("Welcome back!")
                }
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Normal,
                        fontSize = 10.sp
                    )
                ) {
                    append("\nGood to see you again. Log in securely.")
                }
            }

            Text(
                text = loginMessage,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            TextField(
                value = viewModel.email,
                onValueChange = {
                    viewModel.email = it
                    viewModel.clearError()
                },
                label = "Email",
                modifier = fieldModifier,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Email
                )
            )

            TextField(
                value = viewModel.password,
                onValueChange = {
                    viewModel.password = it
                    viewModel.clearError()
                },
                label = "Password",
                modifier = fieldModifier,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Password
                ),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    keyboardController?.hide()
                    viewModel.loginUser {
                        viewModel.refreshSources()
                        viewModel.reset()
                        onLoginSuccess()
                    }
                },
                enabled = !viewModel.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (viewModel.isLoading) "Logging In..." else "Log In")
            }

            val annotatedText = buildAnnotatedString {
                append("Don't have an account? ")

                pushStringAnnotation(
                    tag = "SIGN_UP",
                    annotation = "sign_up"
                )
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline
                    )
                ) {
                    append("Sign Up")
                }
                pop()
            }

            Spacer(modifier = Modifier.height(12.dp))

            ClickableText(
                text = annotatedText,
                style = TextStyle(fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground),
                onClick = { offset ->
                    annotatedText.getStringAnnotations(
                        tag = "SIGN_UP", start = offset, end = offset
                    ).firstOrNull()?.let {
                        navController.navigate(Screen.Register.route)
                    }
                }
            )

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

}