package com.example.frontend.presentation.register.components

data class RegisterState(
    val isLoading: Boolean = false,
    val message: String = "",
    val error: String = ""
)
