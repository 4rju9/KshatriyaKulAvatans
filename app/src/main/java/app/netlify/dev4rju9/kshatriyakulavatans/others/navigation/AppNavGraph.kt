package app.netlify.dev4rju9.kshatriyakulavatans.others.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
}