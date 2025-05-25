package app.netlify.dev4rju9.kshatriyakulavatans.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    val name: String = "",
    val username: String = "",
    @PrimaryKey
    val email: String = "",
    val admin: Boolean = false
)