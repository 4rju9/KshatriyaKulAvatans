package app.netlify.dev4rju9.kshatriyakulavatans.ui.screens.administratorscreen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.netlify.dev4rju9.kshatriyakulavatans.data.models.User
import app.netlify.dev4rju9.kshatriyakulavatans.ui.screens.mainscreen.CompactSearchBar
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AdministratorScreen(
    viewModel: AdminViewModel = hiltViewModel(),
) {
    val users by viewModel.filteredUsers.collectAsState()
    var searchText by remember { mutableStateOf("") }
    var searchJob by remember { mutableStateOf<Job?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
        ) {

            CompactSearchBar(
                value = searchText,
                onValueChange = {
                    searchText = it
                    searchJob?.cancel()
                    searchJob = coroutineScope.launch {
                        delay(300)
                        viewModel.updateSearchQuery(it)
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                onClose = {
                    searchText = ""
                    viewModel.updateSearchQuery("")
                }
            )

            // Refresh Button
            Button(
                onClick = {
                    viewModel.refreshSources()
                    searchText = ""
                    viewModel.updateSearchQuery("")
                },
                modifier = Modifier
                    .height(36.dp)
                    .align(Alignment.CenterVertically),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh Icon",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp)
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
        ) {
            if (users.isEmpty() && searchText.isNotBlank()) {
                item {
                    Text(
                        text = "No users found for \"$searchText\".\nTry another keyword or refresh the list.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
            } else {
                items(users) { user ->
                    UserCard(user) {email, isAdmin ->
                        viewModel.updateRole(email, isAdmin) {
                            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserCard(
    user: User,
    onRoleChange: (String, Boolean) -> Unit
) {
    Log.d("x4rju9", "UserCard: $user")

    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // User's Name
            Text(
                text = if (user.name.isNotEmpty()) user.name else "Unknown",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            // User's username
            Text(
                text = if (user.username.isNotEmpty()) user.username else "Instagram User",
                style = MaterialTheme.typography.titleMedium
            )

            // User's Email
            Text(
                text = if (user.email.isNotEmpty()) user.email else "No Email",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f) // Light gray color from Material theme
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Role and Role Toggle Button
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Display the Role Badge
                RoleBadge(isAdmin = user.admin)

                // Determine Role Change
                val newRole = !user.admin
                val buttonText = if (user.admin) "Demote" else "Promote"
                val buttonColor = if (user.admin) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary

                // Button for Role Change
                Button(
                    onClick = { onRoleChange(user.email, newRole) },
                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
                ) {
                    Text(text = buttonText, color = MaterialTheme.colorScheme.onPrimary) // Text color to match button contrast
                }
            }
        }
    }
}

@Composable
fun RoleBadge(isAdmin: Boolean) {
    val color = if (isAdmin) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
    val label = if (isAdmin) "Admin" else "User"

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = label, color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.labelSmall)
    }
}