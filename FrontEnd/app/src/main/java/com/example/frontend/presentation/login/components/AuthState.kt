package com.example.frontend.presentation.login.components

import com.example.frontend.domain.model.LoginToken

data class AuthState(
    val isLoading: Boolean = true,
    val isAuthorized : Boolean = false, //ako je lista, onda emptyList()
    val error: String = ""
)
