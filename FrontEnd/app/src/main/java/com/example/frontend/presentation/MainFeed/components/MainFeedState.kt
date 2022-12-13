package com.example.frontend.presentation.MainFeed.components

import com.example.frontend.domain.model.Post

data class MainFeedState (
    val isLoading : Boolean = false,
    val posts : List<Post> = emptyList(),
    val error : String = ""
)