package com.example.dolcetto.presentation.cook

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dolcetto.data.repository.OrderRepository
import com.example.dolcetto.data.repository.Resource
import com.example.dolcetto.domain.model.Dish
import com.example.dolcetto.domain.model.Order
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CookViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _ordersState = MutableStateFlow<Resource<List<Order>>>(Resource.Loading())
    val ordersState: StateFlow<Resource<List<Order>>> = _ordersState

    init {
        loadOrders()
    }

    fun loadOrders() {
        viewModelScope.launch {
            orderRepository.getOrders()
                .catch { e ->
                    Log.e("CookViewModel", "Error loading orders", e)
                    _ordersState.value = Resource.Error(e.message ?: "Error loading orders")
                }
                .collect { orders ->
                    Log.d("CookViewModel", "Loaded ${orders.size} orders")
                    _ordersState.value = Resource.Success(orders)
                }
        }
    }

    fun updateOrderStatus(orderId: String, newStatus: String) {
        viewModelScope.launch {
            when (val result = orderRepository.updateOrder(orderId, newStatus)) {
                is Resource.Success -> {
                    Log.d("CookViewModel", "Status updated successfully")
                    loadOrders()
                }
                is Resource.Error -> {
                    Log.e("CookViewModel", "Failed to update: ${result.message}")
                    _ordersState.value = Resource.Error(
                        message = result.message ?: "Failed to update status",
                        data = _ordersState.value.data
                    )
                }
                else -> {}
            }
        }
    }
}