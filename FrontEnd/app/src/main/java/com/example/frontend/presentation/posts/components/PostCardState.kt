package com.example.frontend.presentation.posts.components

import com.example.frontend.domain.model.Post

data class PostCardState(
    val isLoading: Boolean = false,
    val picture: ByteArray = ByteArray(0), //ako je lista, onda emptyList()
    val error: String = ""
)
