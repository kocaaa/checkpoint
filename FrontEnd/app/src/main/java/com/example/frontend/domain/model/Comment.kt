package com.example.frontend.domain.model

data class Comment (
    val id: Long,
    val authorId: Long,
    val authorUsername: String,
    val postId: Long,
    val text: String,
    val subCommentList: List<Comment>,
    val canDelete: Boolean,
    val image: String,
    val date: String
)