package app.netlify.dev4rju9.kshatriyakulavatans.ui.screens.authenticationscreens.registrationscreen

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import app.netlify.dev4rju9.kshatriyakulavatans.R
import app.netlify.dev4rju9.kshatriyakulavatans.others.Screen
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen (
    viewModel: RegisterViewModel = hiltViewModel(),
    navController: NavController,
    onLoginSuccess: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val requestFocusUsername = remember { FocusRequester() }
    val requestFocusEmail = remember { FocusRequester() }
    val requestFocusPassword = remember { FocusRequester() }
    val scaffoldState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(navController.currentBackStackEntry) {
        viewModel.reset()
    }

    val fieldModifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 6.dp)

    Scaffold (
         snackbarHost = { SnackbarHost(hostState = scaffoldState) }
    ) { innerPadding ->
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
                    append("New here?")
                }
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Normal,
                        fontSize = 10.sp
                    )
                ) {
                    append("\nLet’s get started with your research.")
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
                value = viewModel.name,
                onValueChange = { viewModel.name = it },
                label = "Full Name",
                modifier = fieldModifier,
                keyboardActions = KeyboardActions(onNext = { requestFocusUsername.requestFocus() }),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            TextField(
                value = viewModel.username,
                onValueChange = { viewModel.username = it.replace("@", "") },
                label = "Instagram Username",
                modifier = fieldModifier
                    .focusRequester(requestFocusUsername),
                keyboardActions = KeyboardActions(onNext = { requestFocusEmail.requestFocus() }),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            TextField(
                value = viewModel.email,
                onValueChange = { viewModel.email = it },
                label = "Email",
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Email
                ),
                modifier = fieldModifier
                    .focusRequester(requestFocusEmail),
                keyboardActions = KeyboardActions(onNext = { requestFocusPassword.requestFocus() }),
            )

            TextField(
                value = viewModel.password,
                onValueChange = { viewModel.password = it },
                label = "Password",
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Password
                ),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                modifier = fieldModifier
                    .focusRequester(requestFocusPassword)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    keyboardController?.hide()
                    viewModel.onSignUp(onLoginSuccess)
                },
                enabled = !viewModel.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(if (viewModel.isLoading) "Signing Up..." else "Sign Up")
            }

            val annotatedText = buildAnnotatedString {
                append("Already have an account? ")

                pushStringAnnotation(
                    tag = "SIGN_IN",
                    annotation = "sign_in"
                )
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline
                    )
                ) {
                    append("Sign In")
                }
                pop()
            }

            Spacer(modifier = Modifier.height(12.dp))

            ClickableText(
                text = annotatedText,
                style = TextStyle(fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground),
                onClick = { offset ->
                    annotatedText.getStringAnnotations(
                        tag = "SIGN_IN", start = offset, end = offset
                    ).firstOrNull()?.let {
                        navController.navigate(Screen.Login.route)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextField (
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    placeholder: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = MaterialTheme.colorScheme.onBackground) },
        singleLine = singleLine,
        modifier = modifier,
        textStyle = TextStyle(fontSize = 12.sp),
        shape = RoundedCornerShape(20.dp),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
        placeholder = placeholder,
        trailingIcon = trailingIcon,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.primary,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground
        )
    )
}