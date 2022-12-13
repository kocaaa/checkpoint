package com.example.frontend.presentation.map.components

import com.google.maps.android.compose.MapProperties

data class MapState(
    val properties : MapProperties = MapProperties(), //ovde mogu da se proslede parametri kao sto je my location dugme da bude enabled, ali za to onda trebaju permisije
    val isLightMap : Boolean = false
    )
