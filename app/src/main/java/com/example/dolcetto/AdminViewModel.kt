package com.example.dolcetto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val menuRepository: MenuRepository
) : ViewModel() {

    private val _menuState = MutableStateFlow<Resource<List<Dish>>>(Resource.Loading())
    val menuState: StateFlow<Resource<List<Dish>>> = _menuState

    private val _editDishState = MutableStateFlow<Dish?>(null)
    val editDishState: StateFlow<Dish?> = _editDishState.asStateFlow()

    init {
        loadMenu()
    }

    fun loadMenu() {
        viewModelScope.launch {
            menuRepository.getMenu()
                .catch { e ->
                    _menuState.value = Resource.Error<List<Dish>>(message = e.message ?: "Error loading menu")
//                    _menuState.value = Resource.Error(e.message ?: "Error loading menu")
                }
                .collect { dishes ->
                    _menuState.value = Resource.Success(dishes)
                }
        }
    }

    fun setEditDish(dish: Dish?) {
        _editDishState.value = dish
    }

    fun saveDish(dish: Dish) {
        viewModelScope.launch {
            val result = if (dish.id.isEmpty()) {
                menuRepository.addDish(dish)
            } else {
                menuRepository.updateDish(dish)
            }

            when (result) {
                is Resource.Success -> {
                    setEditDish(null)
//                    loadMenu() может быть потом верну
                }
                is Resource.Error -> {
                    _menuState.value = Resource.Error(message = result.message ?: "Error", data = null)
//                    _menuState.value = result
                }
                else -> {}
            }
        }
    }

    fun deleteDish(dishId: String) {
        viewModelScope.launch {
            when (val result = menuRepository.deleteDish(dishId)) {
                is Resource.Success -> loadMenu()
                is Resource.Error -> {
                    _menuState.value = Resource.Error(message = result.message ?: "Error", data = null)
//                    _menuState.value = result
                }
                else -> {}
            }
        }
    }

}