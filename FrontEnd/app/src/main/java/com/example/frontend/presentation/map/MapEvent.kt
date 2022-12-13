package com.example.frontend.presentation.map

import com.example.frontend.domain.model.Location
import com.google.android.gms.maps.model.LatLng

//ovde definisemo neke radnje koje korisnici mogu da obave nad mapom
sealed class MapEvent{
    object ToggleLightMap : MapEvent()
    data class OnMapLongClick(val latLng: LatLng) : MapEvent()
    data class OnInfoWindowLongClick(val location : Location) : MapEvent()
}
