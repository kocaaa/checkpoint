package com.example.frontend.domain.model

data class ProfileData (
    val username : String,
    val followersCount : Int,
    val followingCount : Int,
    val postCount : Int,
    val amFollowing : Boolean
)