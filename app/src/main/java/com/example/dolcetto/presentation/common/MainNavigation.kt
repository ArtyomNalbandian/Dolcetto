package com.example.dolcetto.presentation.common

import android.util.Log
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
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.dolcetto.domain.model.UserData
import com.example.dolcetto.presentation.menu.AdminMenuScreen
import com.example.dolcetto.presentation.auth.AuthViewModel
import com.example.dolcetto.presentation.auth.LoginScreen
import com.example.dolcetto.presentation.auth.RegisterScreen
import com.example.dolcetto.presentation.user.CartScreen
import com.example.dolcetto.presentation.user.DishDetailScreen
import com.example.dolcetto.presentation.user.UserMenuScreen


@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val currentUser by authViewModel.currentUser.collectAsState()
    val isLoading by authViewModel.isLoading.collectAsState()

    // Индикатор загрузки
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
        // Аутентификация
        authGraph(navController, authViewModel)

        // Основной граф
        mainGraph(navController, authViewModel, currentUser)
    }

    // Обработка изменений пользователя
    UserEffect(currentUser, navController)
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

private fun NavGraphBuilder.authGraph(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    navigation(
        startDestination = "login",
        route = "auth"
    ) {
        composable("login") {
            LoginScreen(navController)
        }
        composable("register") {
            RegisterScreen(navController)
        }
    }
}

private fun NavGraphBuilder.mainGraph(
    navController: NavController,
    authViewModel: AuthViewModel,
    currentUser: UserData?
) {
    navigation(
        startDestination = when (currentUser?.role) {
//            "user" -> "user/main"
            "user" -> "user"
//            "admin" -> "admin/main"
            "admin" -> "admin"
//            "kitchen" -> "kitchen/main"
            "kitchen" -> "kitchen"
            else -> "auth"
        },
        route = "main"
    ) {
        userGraph(navController, authViewModel)
        adminGraph(navController, authViewModel)
        kitchenGraph(navController, authViewModel)
    }
}


private fun NavGraphBuilder.userGraph(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    navigation(
        startDestination = "user/main",
        route = "user"
    ) {
        composable("user/main") {
            UserMenuScreen(
                onLogout = { authViewModel.logout() },
                navController = navController
            )
        }
        composable(
            route = "user/dish/{dishId}",
            arguments = listOf(navArgument("dishId") { type = NavType.StringType })
        ) { backStackEntry ->
            val dishId = backStackEntry.arguments?.getString("dishId") ?: ""
            Log.d("testLog", "dishId --- $dishId, route --- $route")
            DishDetailScreen(
                dishId = dishId,
                navController = navController)
        }
        composable("user/cart") {
            CartScreen(navController)
        }
//        composable("user/checkout") {
//            CheckoutScreen(
//                onBack = { navController.popBackStack() },
//                onPaymentSuccess = { navController.navigate("user/orders") }
//            )
//        }
//        composable("user/orders") {
//            OrderHistoryScreen(
//                onBack = { navController.popBackStack() }
//            )
//        }
    }
}

private fun NavGraphBuilder.adminGraph(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    navigation(
        startDestination = "admin/main",
        route = "admin"
    ) {
        composable("admin/main") {
            AdminMenuScreen()
        }
//        composable(
//            route = "admin/edit/{dishId}",
//            arguments = listOf(navArgument("dishId") { type = NavType.StringType })
//        ) { backStackEntry ->
//            val dishId = backStackEntry.arguments?.getString("dishId") ?: ""
//            EditDishScreen(
//                dishId = dishId,
//                onBack = { navController.popBackStack() }
//            )
//        }
    }
}

private fun NavGraphBuilder.kitchenGraph(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    navigation(
        startDestination = "kitchen/main",
        route = "kitchen"
    ) {
        composable("kitchen/main") {
            KitchenStubScreen {  }
//            KitchenScreen(
//                onLogout = { authViewModel.logout() }
//            )
        }
    }
}


@Composable
private fun UserEffect(currentUser: UserData?, navController: NavController) {
    LaunchedEffect(currentUser) {
        when {
            currentUser == null -> {
                navController.navigate("auth") {
                    popUpTo(0)
                }
            }
            currentUser.role == "user" -> navController.navigate("user") {
                popUpTo("main") { inclusive = true }
            }
            currentUser.role == "admin" -> navController.navigate("admin") {
                popUpTo("main") { inclusive = true }
            }
            currentUser.role == "kitchen" -> navController.navigate("kitchen") {
                popUpTo("main") { inclusive = true }
            }
        }
    }
}