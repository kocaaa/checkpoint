package com.example.frontend.presentation.login.components

import com.example.frontend.domain.model.LoginToken

data class LoginState(
    val isLoading: Boolean = false,
    val token: LoginToken? = null, //ako je lista, onda emptyList()
    val error: String = ""
)
