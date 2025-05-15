package com.example.dolcetto.presentation.menu

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.example.dolcetto.domain.model.Dish

@Composable
fun EditDishDialog(
    dish: Dish,
    onDismiss: () -> Unit,
    onSave: (Dish) -> Unit
) {
    var name by remember { mutableStateOf(dish.name) }
    var description by remember { mutableStateOf(dish.description) }
    var price by remember { mutableStateOf(dish.price.toString()) }
    var category by remember { mutableStateOf(dish.category) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (dish.id.isEmpty()) "Add Dish" else "Edit Dish") },
        confirmButton = {
            Button(
                onClick = {
                    val newPrice = price.toDoubleOrNull() ?: 0.0
                    val updatedDish = dish.copy(
                        name = name,
                        description = description,
                        price = newPrice,
                        category = category
                    )
                    onSave(updatedDish)
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Dish Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Price") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}