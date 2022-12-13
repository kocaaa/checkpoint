package com.example.frontend.presentation.newpost

import Constants
import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.frontend.common.Resource
import com.example.frontend.data.remote.dto.LocationDTO
import com.example.frontend.domain.DataStoreManager
import com.example.frontend.domain.use_case.get_locations.SaveLocationUseCase
import com.example.frontend.presentation.newpost.components.NovPostMapState
import com.google.android.gms.maps.model.LatLng
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NovPostMapViewModel @Inject constructor(
    private val saveLocationUseCase: SaveLocationUseCase,
    application: Application,
    ): ViewModel() {
    private val _state = mutableStateOf(NovPostMapState())
    val state: State<NovPostMapState> = _state
    val context = application.baseContext

    fun saveLocation(name: String,position: LatLng,navController: DestinationsNavigator){
        GlobalScope.launch(Dispatchers.Main) {
            var access_token = DataStoreManager.getStringValue(context, "access_token").trim();
            var refresh_token = DataStoreManager.getStringValue(context, "refresh_token").trim();

            saveLocationUseCase("Bearer " + access_token, LocationDTO(0,name,position.latitude,position.longitude)).map { result ->
                when (result) {
                    is Resource.Success -> {
                        println(result.data!!)
                        _state.value = NovPostMapState(result.data!!)
                        Constants.locationId = result.data!!.id
                        navController.popBackStack()
                    }
                    is Resource.Error -> {
                        if(result.message?.contains("403") == true){
                            GlobalScope.launch(Dispatchers.Main){
                                DataStoreManager.deleteAllPreferences(context);
                            }
                        }

                        println("Error1")
                        println(result.message);
                    }
                    is Resource.Loading -> {
                        println("Loading1")
                        println(result.message)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }
}