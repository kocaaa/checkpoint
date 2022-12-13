package com.example.frontend.presentation.map

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.frontend.domain.DataStoreManager
import com.example.frontend.presentation.map.components.MapState
import com.google.android.gms.maps.model.MapStyleOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private var application: Application,
) : ViewModel(){

    private val _state = mutableStateOf(MapState())
    val state : State<MapState> = _state

    val context = application.baseContext
    var access_token  = "";
    var refresh_token = "";

    init {
        GlobalScope.launch(Dispatchers.Main){
            access_token =  DataStoreManager.getStringValue(context, "access_token");
            refresh_token = DataStoreManager.getStringValue(context, "refresh_token");
        }
    }

    fun onEvent(event : MapEvent)
    {
        when(event){
            is MapEvent.ToggleLightMap -> {
                _state.value = _state.value.copy(
                    properties = _state.value.properties.copy(
                        mapStyleOptions = if(_state.value.isLightMap) {
                            null
                        } else MapStyleOptions(MapStyle.json),
                    ),
                    isLightMap = !_state.value.isLightMap
                )
            }
            is MapEvent.OnMapLongClick -> {

            }
            is MapEvent.OnInfoWindowLongClick -> {

            }
        }
    }



}