package com.example.dolcetto.presentation.menu

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dolcetto.domain.model.Dish
import com.example.dolcetto.data.repository.Resource

@Composable
fun AdminMenuScreen(
    viewModel: AdminViewModel = hiltViewModel()
) {
    val menuState by viewModel.menuState.collectAsState()
    val editingDish by viewModel.editDishState.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.setEditDish(Dish()) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Dish")
            }
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
            is Resource.Success -> {
                val dishes = (menuState as Resource.Success<List<Dish>>).data ?: emptyList()

                LazyColumn(modifier = Modifier.padding(padding)) {
                    items(dishes) { dish ->
                        DishItem(
                            dish = dish,
                            onEdit = { viewModel.setEditDish(dish) },
                            onDelete = { viewModel.deleteDish(dish.id) }
                        )
                    }
                }
            }
        }

        editingDish?.let { dish ->
            EditDishDialog(
                dish = dish,
                onDismiss = { viewModel.setEditDish(null) },
                onSave = { updatedDish ->
                    viewModel.saveDish(updatedDish)
                }
            )
        }
    }
}

@Composable
private fun DishItem(
    dish: Dish,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(dish.name, style = MaterialTheme.typography.titleMedium)
                Text(dish.description, style = MaterialTheme.typography.bodyMedium)
                Text("${dish.price} ₽", style = MaterialTheme.typography.titleSmall)
            }

            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, "Edit")
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, "Delete", tint = Color.Red)
            }
        }
    }
}