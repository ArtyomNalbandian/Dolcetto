package com.example.dolcetto.domain.model

import com.google.firebase.firestore.DocumentId

data class Order(
    val id: String = "",
    val orderName: String = "",
    val dishes: List<Dish> = emptyList(),
    val totalPrice: Double = 0.0,
    val status: String = "pending"
)

