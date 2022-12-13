package com.example.frontend.presentation.profile.components

import com.example.frontend.domain.model.ProfileData

data class ProfilePictureState(
    val isLoading: Boolean = false,
    val profilePicture : String = "",
    val error: String = ""
)
