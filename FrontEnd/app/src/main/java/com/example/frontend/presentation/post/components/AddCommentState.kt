package com.example.frontend.presentation.post.components

data class AddCommentState(
    val isLoading: Boolean = false,
    val message: String = "",
    val error: String = ""
)