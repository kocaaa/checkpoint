package com.example.frontend.presentation.post.components

data class DeleteCommentState(
    val isLoading: Boolean = false,
    val message: String = "",
    val error: String = ""
)