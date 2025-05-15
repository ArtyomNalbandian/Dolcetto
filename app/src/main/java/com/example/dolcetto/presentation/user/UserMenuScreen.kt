package com.example.dolcetto.presentation.user


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.dolcetto.data.repository.Resource
import com.example.dolcetto.domain.model.Dish

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserMenuScreen(
    navController: NavController,
    onLogout: () -> Unit,
    viewModel: UserViewModel = hiltViewModel()
) {
    val menuState by viewModel.menuState.collectAsState()
    val cartState by viewModel.cartState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Menu") },
                actions = {
                    IconButton(onClick = { navController.navigate("cart") }) {
                        BadgedBox(badge = {
                            if (cartState is Resource.Success) {
                                val count =
                                    (cartState as Resource.Success).data?.sumOf { it.quantity } ?: 0
                                if (count > 0) Text("$count")
                            }
                        }) {
                            Icon(Icons.Default.ShoppingCart, "Cart")
                        }
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, "Logout")
                    }
                }
            )
        }
    ) { padding ->
        when (menuState) {
            is Resource.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is Resource.Error -> {
                Text(
                    text = (menuState as Resource.Error).message ?: "Error",
                    color = MaterialTheme.colorScheme.error
                )
            }
            is Resource.Success -> (menuState as Resource.Success).data?.let {
                MenuList(
                    dishes = it,
                    padding = padding,
                    onDishClick = { dish ->
                        navController.navigate("user/dish/${dish.id}")
                    }
                )
            }
        }
    }
}

@Composable
private fun MenuList(
    dishes: List<Dish>,
    padding: PaddingValues,
    onDishClick: (Dish) -> Unit
) {

    LazyColumn(contentPadding = padding) {
        items(dishes) { dish ->
            DishCard(
                dish = dish,
                onClick = { onDishClick(dish) }
            )
        }
    }
}

@Composable
private fun DishCard(dish: Dish, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = dish.imageUrl,
                contentDescription = null,
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(dish.name, style = MaterialTheme.typography.titleLarge)
                Text(dish.description, style = MaterialTheme.typography.bodyMedium)
                Text("${dish.price} ₽", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}