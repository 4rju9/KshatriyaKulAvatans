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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.netlify.dev4rju9.kshatriyakulavatans.others.navigation.Screen
import app.netlify.dev4rju9.kshatriyakulavatans.ui.screens.authenticationscreens.loginscreen.LoginScreen
import app.netlify.dev4rju9.kshatriyakulavatans.ui.screens.authenticationscreens.registrationscreen.RegisterScreen
import app.netlify.dev4rju9.kshatriyakulavatans.ui.screens.homescreen.HomeScreen
import app.netlify.dev4rju9.kshatriyakulavatans.ui.screens.mainscreen.MainScreenViewModel
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
                    val startDestination = if (viewModel.user != null) Screen.Home.route
                    else Screen.Login.route
                    MyAppNavHost(
                        this,
                        startDestination = startDestination,
                        isAdmin = viewModel.isAdmin.value
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
    startDestination: String = Screen.Register.route,
    isAdmin: Boolean
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
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            }
        }
        composable(Screen.Home.route) {
            HomeScreen(context, navController, isAdmin)
        }
    }
}