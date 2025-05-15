package com.example.dolcetto.presentation.user

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.dolcetto.data.repository.Resource
import com.example.dolcetto.domain.model.Dish
import com.example.dolcetto.presentation.menu.dish_detail.DishDetailViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DishDetailScreen(
    dishId: String,
    viewModel: UserViewModel = hiltViewModel(),
    dishDetailViewModel: DishDetailViewModel = hiltViewModel(),
    navController: NavController
) {

    LaunchedEffect(dishId) {
        dishDetailViewModel.loadDish(dishId)
    }

    val dishState by dishDetailViewModel.dishState.collectAsState()

//    val dish by remember(dishId) {
//        derivedStateOf {
//            (viewModel.menuState.value as? Resource.Success)?.data?.find { it.id == dishId }
//        }
//    }

    Log.d("testLog", "dishDetailId --- $dishId, dish - $dishState")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(dishState.data?.name ?: "") },
//                title = { Text(dish?.name ?: "") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { dishState.let { it.data?.let { it1 -> viewModel.addToCart(it1) } } },
                //onClick = { dish?.let { viewModel.addToCart(it) } },
                icon = { Icon(Icons.Default.Add, "Add to cart") },
                text = { Text("Add to Cart") }
            )
        }
    ) { padding ->
        when (dishState) {
            is Resource.Loading -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is Resource.Error -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text((dishState as Resource.Error).message ?: "Error", color = MaterialTheme.colorScheme.error)
                }
            }
            is Resource.Success -> {
                val dish = (dishState as Resource.Success).data
                DishDetailContent(dish = dish!!, modifier = Modifier.padding(padding))
            }
        }
//        if (dish == null) {
//            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
//                Text("Dish not found")
//            }
//        } else {
//            DishDetailContent(dish!!, modifier = Modifier.padding(padding))
//        }
    }
}

@Composable
private fun DishDetailContent(dish: Dish, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        AsyncImage(
            model = dish.imageUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(RoundedCornerShape(8.dp))
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(dish.name, style = MaterialTheme.typography.headlineMedium)
        Text(dish.description, style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(8.dp))

        Text("Price: ${dish.price} ₽", style = MaterialTheme.typography.titleLarge)
    }
}