package app.netlify.dev4rju9.kshatriyakulavatans.data.models

data class User(
    val name: String = "",
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val admin: Boolean = false
)