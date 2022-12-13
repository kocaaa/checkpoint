package com.example.frontend.presentation.profile_settings.components

data class UserInfoChangeState (
    val isLoading: Boolean = false,
    val message: String ?= "",
    val error: String = ""
)