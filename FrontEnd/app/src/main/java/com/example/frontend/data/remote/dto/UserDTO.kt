package com.example.frontend.data.remote.dto

import com.example.frontend.domain.model.User

data class UserDTO(
    val id:Long,
    val email:String,
    val username:String,
    val password:String
)


fun UserDTO.toUser() : User{
    return User(
        id = id,
        email = email,
        username = username,
        password = password
    )
}
