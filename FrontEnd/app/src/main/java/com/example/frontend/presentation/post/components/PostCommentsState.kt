package com.example.frontend.presentation.post.components

import com.example.frontend.domain.model.Comment

data class PostCommentsState(
    val isLoading: Boolean = false,
    val comments: List<Comment>? = emptyList(),
    val error: String = ""
)