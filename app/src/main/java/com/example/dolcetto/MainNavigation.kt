package com.example.dolcetto

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val viewModel: AuthViewModel = hiltViewModel()
    val currentUser by viewModel.currentUser.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    NavHost(
        navController = navController,
        startDestination = if (currentUser != null) "main" else "auth"
    ) {
        navigation(startDestination = "login", route = "auth") {
            composable("login") { LoginScreen(navController) }
            composable("register") { RegisterScreen(navController) }
        }

        navigation(startDestination = "role", route = "main") {
            composable("role") {
                when (currentUser?.role) {
                    "user" -> UserStubScreen { viewModel.logout() }
                    "kitchen" -> KitchenStubScreen { viewModel.logout() }
                    "admin" -> AdminStubScreen { viewModel.logout() }
                    else -> { /* Без понятия что тут может быть */ }
                }
            }
        }
    }

    LaunchedEffect(currentUser) {
        if (currentUser == null) {
            navController.navigate("auth") {
                popUpTo(0)
            }
        }
    }
}

@Composable
fun UserStubScreen(onLogout: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("User Interface")
        Button(onClick = onLogout) {
            Text("Logout")
        }
    }
}

@Composable
fun KitchenStubScreen(onLogout: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Kitchen Interface")
        Button(onClick = onLogout) {
            Text("Logout")
        }
    }
}

@Composable
fun AdminStubScreen(onLogout: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Admin Interface")
        Button(onClick = onLogout) {
            Text("Logout")
        }
    }
}