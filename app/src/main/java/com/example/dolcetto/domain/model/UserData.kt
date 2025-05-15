package com.example.dolcetto.domain.model

import androidx.annotation.Keep

@Keep
data class UserData(
    val userId: String = "",
    val email: String = "",
    val role: String = "user",
    val cart: List<CartItem> = emptyList(),
) {
    constructor() : this("", "", "user", emptyList())
}

data class TestUserData(
    val userId: String = "",
    val email: String = "",
    val role: String = "user",
    val cart: List<CartItem> = emptyList(),
)