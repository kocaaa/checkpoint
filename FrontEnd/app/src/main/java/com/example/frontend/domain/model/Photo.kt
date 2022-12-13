package com.example.frontend.domain.model

import com.example.frontend.data.remote.dto.BinaryPhoto

data class Photo(
    val id:String,
    val order:Int,
    val postId:Long,
    val photo: BinaryPhoto
)
