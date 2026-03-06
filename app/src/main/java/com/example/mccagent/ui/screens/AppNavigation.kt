package com.example.mccagent.ui.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mccagent.ui.screens.HomeScreen
import com.example.mccagent.ui.screens.LoginScreen
import com.example.mccagent.ui.screens.MessagesScreen
import com.example.mccagent.ui.screens.RealTimeMessagesScreen
import com.example.mccagent.ui.screens.SettingsScreen
import com.example.mccagent.ui.screens.SplashScreen
import com.example.mccagent.utils.SecureSessionStorage
import com.example.mccagent.utils.SessionManager

@Composable
fun AppNavigation(context: Context) {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(
                onFinish = {
                    val tokenInicial = SecureSessionStorage.obtenerToken(context)
                    val destino = if (tokenInicial.isNullOrBlank()) "login" else "home"
                    navController.navigate(destino) {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

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
                onMessages = {
                    navController.navigate("messages")
                },
                onRealTime = {
                    navController.navigate("real_time")
                },
                onSettings = {
                    navController.navigate("settings")
                }
            )
        }

        composable("messages") {
            MessagesScreen(
                onHome = {
                    navController.navigate("home")
                },
                onRealTime = {
                    navController.navigate("real_time")
                },
                onSettings = {
                    navController.navigate("settings")
                }
            )
        }

        composable("real_time") {
            RealTimeMessagesScreen(
                onHome = {
                    navController.navigate("home")
                },
                onMessages = {
                    navController.navigate("messages")
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
