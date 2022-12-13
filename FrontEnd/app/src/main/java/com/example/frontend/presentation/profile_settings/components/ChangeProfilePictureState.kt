package com.example.frontend.presentation.profile_settings.components

import android.graphics.Bitmap

data class ChangeProfilePictureState (
    val isLoading: Boolean = false,
    val picture: Bitmap ?= null,
    val error: String = ""
)