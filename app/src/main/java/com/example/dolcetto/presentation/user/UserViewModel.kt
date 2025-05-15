package com.example.dolcetto.presentation.user

import androidx.lifecycle.ViewModel
import com.example.dolcetto.data.repository.CartRepository
import com.example.dolcetto.data.repository.MenuRepository
import com.example.dolcetto.data.repository.Resource
import com.example.dolcetto.domain.model.CartItem
import com.example.dolcetto.domain.model.Dish
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.catch



@HiltViewModel
class UserViewModel @Inject constructor(
    private val menuRepo: MenuRepository,
    private val cartRepo: CartRepository
) : ViewModel() {

    private val _menuState = MutableStateFlow<Resource<List<Dish>>>(Resource.Loading())
    val menuState: StateFlow<Resource<List<Dish>>> = _menuState

    private val _cartState = MutableStateFlow<Resource<List<CartItem>>>(Resource.Loading())
    val cartState: StateFlow<Resource<List<CartItem>>> = _cartState

    init {
        loadMenu()
        loadCart()
    }

    private fun loadMenu() {
        viewModelScope.launch {
            menuRepo.getMenu()
                .catch { e ->
                    _menuState.value = Resource.Error(e.message ?: "Error loading menu")
                }
                .collect { dishes ->
                    _menuState.value = Resource.Success(dishes)
                }
        }
    }

    private fun loadCart() {
        viewModelScope.launch {
            cartRepo.getCart()
                .catch { e ->
                    _cartState.value = Resource.Error(e.message ?: "Error loading cart")
                }
                .collect { cart ->
                    _cartState.value = Resource.Success(cart)
                }
        }
    }

    fun addToCart(dish: Dish) {
        viewModelScope.launch {
            val cartItem = CartItem(
                dishId = dish.id,
                price = dish.price,
                name = dish.name,
                imageUrl = dish.imageUrl
            )
            cartRepo.addToCart(cartItem)
        }
    }

    fun updateCartItem(item: CartItem) {
        viewModelScope.launch {
            cartRepo.updateCartItem(item)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            cartRepo.clearCart()
        }
    }

}