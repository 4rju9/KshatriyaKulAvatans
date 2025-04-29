package app.netlify.dev4rju9.kshatriyakulavatans

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.netlify.dev4rju9.kshatriyakulavatans.ui.screens.authenticationscreens.loginscreen.LoginScreen
import app.netlify.dev4rju9.kshatriyakulavatans.ui.screens.authenticationscreens.registrationscreen.RegisterScreen
import app.netlify.dev4rju9.kshatriyakulavatans.ui.screens.mainscreen.MainScreen
import app.netlify.dev4rju9.kshatriyakulavatans.ui.screens.mainscreen.MainScreenViewModel
import app.netlify.dev4rju9.kshatriyakulavatans.others.Screen
import app.netlify.dev4rju9.kshatriyakulavatans.ui.screens.addsourcescreen.AddSourceScreen
import app.netlify.dev4rju9.kshatriyakulavatans.ui.theme.KshatriyakulavatansTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KshatriyakulavatansTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val viewModel = hiltViewModel<MainScreenViewModel>()
                    val startDestination = if (viewModel.user != null) Screen.Main.route
                    else Screen.Login.route
                    MyAppNavHost(
                        this,
                        startDestination = startDestination
                    )
                }
            }
        }
    }
}

@Composable
fun MyAppNavHost(
    context: Context,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Register.route
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Register.route) {
            RegisterScreen(
                navController = navController)
            {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Register.route) { inclusive = true }
                }
            }
        }
        composable(Screen.Login.route) {
            LoginScreen(
                navController = navController
            ) {
                navController.navigate(Screen.Main.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            }
        }
        composable(Screen.Main.route) {
            MainScreen(
                context,
                navController,
                hiltViewModel()
            )
        }
        composable(Screen.AddSource.route) {
            AddSourceScreen(
                navController = navController
            )
        }
    }
}