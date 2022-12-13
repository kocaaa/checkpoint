package com.example.frontend.data.remote.dto

import com.example.frontend.domain.model.LoginToken

data class LoginDTO(
    val access_token : String,
    val refresh_token : String
)

fun LoginDTO.toLoginToken() : LoginToken {
    return LoginToken(
        access_token = access_token,
        refresh_token = refresh_token
    )
}