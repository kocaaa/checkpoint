package com.example.frontend.presentation.post.components

import com.example.frontend.domain.model.Post

data class PostState(
    val isLoading: Boolean = false,
    val post: Post? = null,
    val error: String = ""
)
