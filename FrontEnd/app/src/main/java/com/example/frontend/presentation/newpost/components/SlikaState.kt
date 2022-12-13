package com.example.frontend.presentation.newpost.components

import android.graphics.Bitmap

data class SlikaState(
    val error: String = "",
    val isLoading: Boolean = true,
    val slika: Bitmap
)
