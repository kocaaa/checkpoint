package com.example.frontend.presentation.profile_settings.components

import com.example.frontend.domain.model.User

data class ProfilePictureState (
    val isLoading: Boolean = false,
    val picture: String = "",
    val error: String = ""
)