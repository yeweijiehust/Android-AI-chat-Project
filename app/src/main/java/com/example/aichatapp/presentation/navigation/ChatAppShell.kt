package com.example.aichatapp.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.aichatapp.presentation.chat.ChatScreen
import com.example.aichatapp.presentation.home.HomeScreen
import com.example.aichatapp.presentation.settings.SettingsScreen

@Composable
fun ChatAppShell() {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    // Observe the current back stack to determine which screen is active
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route

    // Logic to hide the bottom bar on the Chat screen
    val showBottomBar = currentRoute == Screen.Home.route || currentRoute == Screen.Settings.route

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    val screens = listOf(Screen.Home, Screen.Settings)

                    screens.forEach { screen ->
                        NavigationBarItem(
                            icon = {
                                screen.icon?.let {
                                    Icon(imageVector = it, contentDescription = screen.title)
                                }
                            },
                            label = { Text(screen.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    // Pop up to the start destination to avoid building up a large stack
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    // Avoid multiple copies of the same destination
                                    launchSingleTop = true
                                    // Restore state when reselecting a previously selected item
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        // The NavHost controls swapping out the screens
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {

            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigate = { route -> navController.navigate(route) },
                    onShowSnackbar = { msg -> snackbarHostState.showSnackbar(msg) }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    onShowSnackbar = { msg -> snackbarHostState.showSnackbar(msg) }
                )
            }

            composable(Screen.Chat.route) { backStackEntry ->
                // The sessionId is automatically extracted by Hilt's SavedStateHandle in the ViewModel
                ChatScreen(
                    onNavigateUp = { navController.navigateUp() },
                    onShowSnackbar = { msg -> snackbarHostState.showSnackbar(msg) }
                )
            }
        }
    }
}