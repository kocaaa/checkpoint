package com.example.frontend.domain.model

import java.io.InputStream

data class Video(
    val postId:Long,
    val order:Int,
    val inputStream:InputStream
)
