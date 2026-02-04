package com.example.mccagent.ui.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import com.example.mccagent.ui.screens.HomeScreen
import com.example.mccagent.ui.screens.LoginScreen
import com.example.mccagent.ui.screens.SettingsScreen
import com.example.mccagent.utils.SessionManager

@Composable
fun AppNavigation(context: Context) {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onOpenSettings = {
                    navController.navigate("settings")
                }
            )
        }
        composable("home") {
            HomeScreen(
                onLogout = {
                    SessionManager.logout(context)

                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onSettings = {
                    navController.navigate("settings")
                }
            )
        }
        composable("settings") {
            SettingsScreen(navController = navController, context = context)
        }
    }
}
