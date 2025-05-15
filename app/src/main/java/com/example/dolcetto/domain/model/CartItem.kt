package com.example.dolcetto.domain.model

data class CartItem(
    val dishId: String,
    var quantity: Int = 1,
    val price: Double = 0.0,
    val name: String = "",
    val imageUrl: String = "",
) {
    constructor() : this("", 1, 0.0, "", "")
}
