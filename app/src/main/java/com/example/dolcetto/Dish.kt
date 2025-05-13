package com.example.dolcetto

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Dish(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val category: String = "",
    val imageUrl: String = "",
    @ServerTimestamp
    val createdAt: Date? = null,
)
