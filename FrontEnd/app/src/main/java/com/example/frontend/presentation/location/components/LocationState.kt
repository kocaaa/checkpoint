package com.example.frontend.presentation.location.components

import com.example.frontend.domain.model.Location

data class LocationState(
    val isLoading: Boolean = false,
    val locations: List<Location> = emptyList(), //ako je lista, onda emptyList()
    val error: String = ""
)
