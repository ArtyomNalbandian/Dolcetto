package com.example.dolcetto.presentation.cook

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dolcetto.data.repository.Resource
import com.example.dolcetto.domain.model.Order


@Composable
fun CookMenuScreen(
    viewModel: CookViewModel = hiltViewModel()
) {
    val ordersState by viewModel.ordersState.collectAsState()


    Scaffold { padding ->
        when (ordersState) {
            is Resource.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is Resource.Error -> {
                Text(
                    text = (ordersState as Resource.Error).message ?: "Error",
                    color = MaterialTheme.colorScheme.error
                )
            }
            is Resource.Success -> {
                val orders = (ordersState as Resource.Success<List<Order>>).data ?: emptyList()

                LazyColumn(modifier = Modifier.padding(padding)) {
                    items(orders) { order ->
                        OrderItem(
                            order = order,
                            onStatusChange = { newStatus ->
                                viewModel.updateOrderStatus(order.id, newStatus)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderItem(
    order: Order,
    onStatusChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Заказ #${order.id.take(5)}", style = MaterialTheme.typography.titleMedium)
                Text("${order.totalPrice} ₽", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(8.dp))

            order.dishes.forEach { dish ->
                Text("• ${dish.name} (${dish.price} ₽)")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Статус:", style = MaterialTheme.typography.bodyMedium)

                var expanded by remember { mutableStateOf(false) }
                val statuses = listOf("pending", "preparing", "ready", "completed")

                Box {
                    OutlinedButton(onClick = { expanded = true }) {
                        Text(order.status)
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        statuses.forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status) },
                                onClick = {
                                    onStatusChange(status)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
