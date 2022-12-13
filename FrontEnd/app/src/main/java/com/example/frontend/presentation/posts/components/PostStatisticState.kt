package com.example.frontend.presentation.posts.components

data class PostStatisticState (
    val isLoading: Boolean = false,
    val count: Int = 0,
    val error: String = ""
)