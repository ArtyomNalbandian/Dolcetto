package com.example.dolcetto.presentation.menu.dish_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dolcetto.data.repository.MenuRepository
import com.example.dolcetto.data.repository.Resource
import com.example.dolcetto.domain.model.Dish
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DishDetailViewModel @Inject constructor(
    private val menuRepo: MenuRepository
) : ViewModel() {

    private val _dishState = MutableStateFlow<Resource<Dish>>(Resource.Loading())
    val dishState: StateFlow<Resource<Dish>> = _dishState

    fun loadDish(dishId: String) {
        viewModelScope.launch {
            menuRepo.getDishById(dishId).collect { result ->
                _dishState.value = result
            }
        }
    }

}