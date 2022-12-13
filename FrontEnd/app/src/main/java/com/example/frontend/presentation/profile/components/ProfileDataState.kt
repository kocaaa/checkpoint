package com.example.frontend.presentation.profile.components

import com.example.frontend.domain.model.ProfileData

data class ProfileDataState(
    val isLoading: Boolean = false,
    val profileData: ProfileData? = null,
    val error: String = ""
)
