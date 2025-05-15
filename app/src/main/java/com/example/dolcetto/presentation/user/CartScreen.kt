package com.example.dolcetto.presentation.user

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
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
import com.example.dolcetto.domain.model.CartItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController,
    viewModel: UserViewModel = hiltViewModel()
) {
    val cartState by viewModel.cartState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cart") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        },
        bottomBar = {
            CartBottomBar(viewModel, cartState)
        }
    ) { padding ->
        when (cartState) {
            is Resource.Loading<*> -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is Resource.Error<*> -> {
                Text(
                    text = (cartState as Resource.Error<*>).message ?: "Error",
                    color = MaterialTheme.colorScheme.error
                )
            }
            is Resource.Success<*> -> CartList(
                items = (cartState as Resource.Success<*>).data as List<CartItem>,
                padding = padding,
                onUpdateItem = viewModel::updateCartItem
            )
        }
    }
}

@Composable
private fun CartList(
    items: List<CartItem>,
    padding: PaddingValues,
    onUpdateItem: (CartItem) -> Unit
) {
    LazyColumn(contentPadding = padding) {
        items(items) { item ->
            CartItemRow(
                item = item,
                onUpdate = onUpdateItem
            )
        }
    }
}

@Composable
private fun CartItemRow(item: CartItem, onUpdate: (CartItem) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = item.imageUrl,
            contentDescription = null,
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(item.name, style = MaterialTheme.typography.titleMedium)
            Text("${item.price} ₽ x ${item.quantity}", style = MaterialTheme.typography.bodyMedium)
            Text("Total: ${item.price * item.quantity} ₽", style = MaterialTheme.typography.titleSmall)
        }

        IconButton(onClick = {
            onUpdate(item.copy(quantity = item.quantity + 1))
        }) {
            Icon(Icons.Default.Add, "Increase")
        }

        Text("${item.quantity}")

        IconButton(onClick = {
            if (item.quantity > 1) {
                onUpdate(item.copy(quantity = item.quantity - 1))
            }
        }) {
            Icon(Icons.Default.Remove, "Decrease")
        }
    }
}

@Composable
private fun CartBottomBar(viewModel: UserViewModel, cartState: Resource<List<CartItem>>) {
    val total = when (cartState) {
        is Resource.Success -> cartState.data?.sumOf { it.price * it.quantity } ?: 0
        else -> 0.0
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Total: $total ₽",
            style = MaterialTheme.typography.headlineSmall
        )
        Button(onClick = { /* TODO: Navigate to checkout */ }) {
            Text("Checkout")
        }
        IconButton(onClick = { viewModel.clearCart() }) {
            Icon(Icons.Default.Delete, "Clear cart")
        }
    }
}