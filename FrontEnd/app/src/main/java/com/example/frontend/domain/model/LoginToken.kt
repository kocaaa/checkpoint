package com.example.frontend.domain.model

data class LoginToken(
    val access_token : String,
    val refresh_token : String
)
