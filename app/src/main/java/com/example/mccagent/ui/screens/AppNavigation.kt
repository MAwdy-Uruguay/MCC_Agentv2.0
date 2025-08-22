package com.example.mccagent.ui.navigation

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import com.example.mccagent.services.SMSService
import com.example.mccagent.ui.screens.HomeScreen
import com.example.mccagent.ui.screens.LoginScreen
import com.example.mccagent.ui.screens.SettingsScreen

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
                    val prefs = context.getSharedPreferences("mcc_prefs", Context.MODE_PRIVATE)
                    prefs.edit().remove("token").apply()

                    val stopIntent = Intent(context, com.example.mccagent.services.SMSService::class.java)
                    context.stopService(stopIntent)

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
