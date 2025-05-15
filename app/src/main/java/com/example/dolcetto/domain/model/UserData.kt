package com.example.dolcetto.domain.model

data class UserData(
    val userId: String = "",
    val email: String = "",
    val role: String = "user",
    val cart: List<CartItem> = emptyList(),
)

data class TestUserData(
    val userId: String = "",
    val email: String = "",
    val role: String = "user",
    val cart: List<CartItem> = emptyList(),
)