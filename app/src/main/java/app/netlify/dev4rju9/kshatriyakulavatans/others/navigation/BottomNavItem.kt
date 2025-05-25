package app.netlify.dev4rju9.kshatriyakulavatans.others.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Kings : BottomNavItem("kings", Icons.Default.Star, "Kings")
    object Add : BottomNavItem("add", Icons.Default.AddCircle, "Add")
    object Articles : BottomNavItem("articles", Icons.Default.Home, "Articles")
    object Profile : BottomNavItem("profile", Icons.Default.Person, "Profile")
    object Administrator : BottomNavItem("admin", Icons.Default.Info, "Admin")
}