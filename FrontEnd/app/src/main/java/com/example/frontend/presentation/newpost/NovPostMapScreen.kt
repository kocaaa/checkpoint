package com.example.frontend.presentation.newpost

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.frontend.presentation.map.MapStyle
import com.example.frontend.presentation.map.updateCamera
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import android.location.Geocoder
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun NovPostMapScreen (
    navigator: DestinationsNavigator,
    viewModel: NovPostMapViewModel =  hiltViewModel()
) {
    val context = LocalContext.current
    val uiSettings = remember {
        MapUiSettings(zoomControlsEnabled = false)
    }
    var markerLatLng = remember {
        mutableStateOf<LatLng?>(null)
    }
    var markerPOI = remember {
        mutableStateOf<PointOfInterest?>(null)
    }

    val camPosState = rememberCameraPositionState {
        position = CameraPosition( LatLng(50.0,25.0),5F,0F,0F)
    }
    val builder = LatLngBounds.Builder()
    var imeLokacije = remember {
        mutableStateOf("")
    }
    val localDensity = LocalDensity.current
    var mapWidth by remember {
        mutableStateOf(44)
    }

    var mapHeight by remember {
        mutableStateOf(20)
    }
    Column(
        Modifier.fillMaxSize()
    ) {
        Button(onClick = { navigator.popBackStack() }, Modifier.wrapContentSize()) {
            Text(text = "Back")
        }
        Row(Modifier.fillMaxWidth().height(50.dp)) {

            TextField(
                value = imeLokacije.value, onValueChange = {
                    if (markerLatLng.value != null) {
                        imeLokacije.value = it
                    }
                },
                label = {Text("Location name")},
                singleLine = true,
            )

            Button(onClick = {
                if(markerLatLng.value != null) {
                    viewModel.saveLocation(imeLokacije.value,markerLatLng.value!!,navigator)
                }
                else{
                    viewModel.saveLocation(markerPOI.value!!.name,markerPOI.value!!.latLng,navigator)
                }
            },
                Modifier.wrapContentSize(),
                enabled = ((markerPOI.value!=null|| markerLatLng.value!= null) && imeLokacije.value != "")
            ) {
                Text("Set location")
            }

        }

        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .onGloballyPositioned { coords ->
                    mapWidth = with(localDensity) { coords.size.width }
                    mapHeight = with(localDensity) { coords.size.height }
                },
            uiSettings = uiSettings,
            onMapLoaded = {

            },
            cameraPositionState = camPosState,
            onMapClick = {
                markerLatLng.value = it
                if(markerPOI.value != null){
                    imeLokacije.value = ""
                    markerPOI.value = null
                }
                else{
                    imeLokacije.value = imeLokacije.value
                }

            },
            onPOIClick = {
                markerLatLng.value = null
                markerPOI.value = it
                imeLokacije.value = it.name
            },
        ) {
            val markerState : MarkerState = rememberMarkerState()
            if (markerLatLng.value != null) {
                markerState.position = markerLatLng.value!!
                Marker(state = markerState, title = imeLokacije.value)
            }
            if (markerPOI.value != null) {
                markerState.position = markerPOI.value!!.latLng
                Marker(state = markerState, title = markerPOI.value!!.name)
            }
        }
    }
}


