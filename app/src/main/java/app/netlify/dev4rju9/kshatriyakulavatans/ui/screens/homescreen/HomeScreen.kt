package app.netlify.dev4rju9.kshatriyakulavatans.ui.screens.homescreen

import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import app.netlify.dev4rju9.kshatriyakulavatans.others.navigation.BottomNavItem
import app.netlify.dev4rju9.kshatriyakulavatans.ui.screens.addsourcescreen.AddSourceScreen
import app.netlify.dev4rju9.kshatriyakulavatans.ui.screens.administratorscreen.AdministratorScreen
import app.netlify.dev4rju9.kshatriyakulavatans.ui.screens.kingscreen.KingScreen
import app.netlify.dev4rju9.kshatriyakulavatans.ui.screens.mainscreen.MainScreen
import app.netlify.dev4rju9.kshatriyakulavatans.ui.screens.profilescreen.ProfileScreen

@Composable
fun HomeScreen(
    context: Context,
    rootNavController: NavHostController,
    isAdmin: Boolean
) {
    val navController = rememberNavController()
    val items = when (isAdmin) {
        true -> mutableListOf(
            BottomNavItem.Articles,
            BottomNavItem.Kings,
            BottomNavItem.Add,
            BottomNavItem.Profile,
            BottomNavItem.Administrator
        )
        false -> mutableListOf(
            BottomNavItem.Articles,
            BottomNavItem.Add,
            BottomNavItem.Kings,
            BottomNavItem.Profile
        )
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController, items = items)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Articles.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Articles.route) {
                MainScreen(context, rootNavController)
            }
            composable(BottomNavItem.Add.route) {
                AddSourceScreen(navController = navController)
            }
            composable(BottomNavItem.Kings.route) {
                KingScreen()
            }
            composable(BottomNavItem.Profile.route) {
                ProfileScreen()
            }
            if (isAdmin) {
                composable(BottomNavItem.Administrator.route) {
                    AdministratorScreen()
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    items: List<BottomNavItem>
) {
    NavigationBar(
        tonalElevation = 6.dp,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        items.forEach { item ->
            val currentDestination = navController.currentBackStackEntryAsState().value?.destination?.route
            val selected = currentDestination == item.route

            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (currentDestination != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(16.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                alwaysShowLabel = true
            )
        }
    }
}