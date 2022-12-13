package com.example.frontend.presentation.profile_settings.components

import com.example.frontend.domain.model.User

data class ProfileSettingsUserState (
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String = ""
)