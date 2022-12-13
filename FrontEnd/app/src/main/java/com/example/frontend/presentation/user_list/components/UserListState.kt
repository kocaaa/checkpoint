package com.example.frontend.presentation.user_list.components

import com.example.frontend.data.remote.dto.UserDetailedDTO

data class UserListState(
    val isLoading : Boolean = false,
    val users : List<UserDetailedDTO>? = emptyList(),
    val error : String = ""
)