package app.netlify.dev4rju9.kshatriyakulavatans.others

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Main : Screen("main")
    object AddSource : Screen("add_source_screen")
}