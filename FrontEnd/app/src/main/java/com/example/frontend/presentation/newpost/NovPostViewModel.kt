package com.example.frontend.presentation.newpost

import Constants
import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.frontend.common.Resource
import com.example.frontend.data.remote.dto.LocationDTO
import com.example.frontend.domain.DataStoreManager
import com.example.frontend.domain.model.Location
import com.example.frontend.domain.use_case.add_post.AddPhotoUseCase
import com.example.frontend.domain.use_case.add_post.AddPostUseCase
import com.example.frontend.domain.use_case.get_locations.GetAllLocationsUseCase
import com.example.frontend.domain.use_case.get_locations.GetLocationsKeywordUseCase
import com.example.frontend.domain.use_case.get_locations.SaveLocationUseCase
import com.example.frontend.presentation.destinations.MainLocationScreenDestination
import com.example.frontend.presentation.destinations.NovPostScreenDestination
import com.example.frontend.presentation.destinations.PostScreenDestination
import com.example.frontend.presentation.newpost.components.NovPostMapState
import com.example.frontend.presentation.newpost.components.NovPostState
import com.example.frontend.presentation.newpost.components.SlikaState
import com.example.frontend.presentation.posts.components.PostsState
import com.example.frontend.presentation.user_list.components.UserListState
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PointOfInterest
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class NovPostViewModel@Inject constructor(
    private val addPostUseCase: AddPostUseCase,
    private val addPhotoUseCase: AddPhotoUseCase,
    private val getAllLocationsUseCase: GetAllLocationsUseCase,
    private val getLocationsKeywordUseCase: GetLocationsKeywordUseCase,
    private val saveLocationUseCase: SaveLocationUseCase,
    application: Application,

): ViewModel() {
    private var _state = mutableStateOf(NovPostState())
    val state: State<NovPostState> = _state

    private val _locationState = mutableStateOf(NovPostMapState())
    val locationState : State<NovPostMapState> = _locationState;

    val context = application.baseContext

    var access_token = "";
    var refresh_token = "";

    val description = mutableStateOf("")
    val location = mutableStateOf(Location(0,"",0.0,0.0));

    var markerLatLng = mutableStateOf<LatLng?>(null)
    var markerPOI =  mutableStateOf<PointOfInterest?>(null)
    var imeLokacije = mutableStateOf("")

    init {
        GlobalScope.launch(Dispatchers.Main){
            access_token = DataStoreManager.getStringValue(context, "access_token").trim();
            refresh_token = DataStoreManager.getStringValue(context, "access_token").trim();

            ucitajLokacije("")
        }
    }

    fun savePost(navigator: DestinationsNavigator, description: String, locationId: Long) {
        Log.d("SAVE POST", "Selected location id ${locationId}")
        Log.d("SAVE POST", "Description is ${description}");
        var desc = "";
        if(description == "")
            desc = " ";
        else
            desc = description;

        addPostUseCase("Bearer " + access_token, desc, locationId).map { result ->
            when (result) {
                is Resource.Success -> {
                    var i = 0
                    Log.d("SAVE POST", "Adding photos");

                    Log.d("SAVE POST", "Slike koje treba dodati su ${_state.value.slike.toString()}")

                    for (photo: SlikaState in _state.value.slike) {
                        addPhoto(navigator, result.data!!.toLong(), i, photo)
                        i++;
                    }
                }
                is Resource.Error -> {
                    Log.d("SAVE POST ERROR", "Error is ${result.message}")
                    println(result.message)
                    _state.value = NovPostState(error = result.message ?: "Unexpected error");
                }
                is Resource.Loading -> {
                    println(result.message)
                    val slike = _state.value.slike;
                    val lokacije = _state.value.lokacije;
                    _state.value = NovPostState(isLoading = true, slike = slike, lokacije = lokacije);
                }
            }
        }.launchIn(viewModelScope)
    }

    fun addPhoto(navigator : DestinationsNavigator, postId: Long, order: Int, slikaState: SlikaState) {
        val path = context.getExternalFilesDir(null)!!.absolutePath
        val tempFile = File(path, "tempFileName${postId}-${order}.jpg")
        val fOut = FileOutputStream(tempFile)
        slikaState.slika.compress(Bitmap.CompressFormat.JPEG, 85, fOut)
        fOut.flush()
        fOut.close()
        val file = MultipartBody.Part.createFormData(
            "photo", tempFile.name,
            RequestBody.create(MediaType.parse("image/*"), tempFile)
        )

        Log.d("ADD PHOTO", "Adding images");
        addPhotoUseCase("Bearer " + access_token, postId, order, file).map { result ->
            when (result) {
                is Resource.Success -> {
                    tempFile.delete()
                    var flag = true

                    var lista = replaceSlikaState(slikaState.slika)
                    println(lista)
                    for (i: SlikaState in lista) {
                        if (i.isLoading) {
                            flag = false
                            break;
                        }
                    }
                    if (flag == true) {
                        //toast
                        Toast.makeText(
                            context,
                            "Post successfully added",
                            Toast.LENGTH_LONG
                        ).show();

                        navigator.popBackStack()
                        navigator.navigate(
                            PostScreenDestination(postId)
                        )
                    } else {
                        println("Nisu sve poslate")
                    }
                }
                is Resource.Error -> {
                    _state.value = NovPostState(error = result.message ?: "Unexpected error");
                    if(result.message?.contains("403") == true){
                        GlobalScope.launch(Dispatchers.Main){
                            DataStoreManager.deleteAllPreferences(context);
                        }
                    }

                    println("Greska" + result.message)
                    tempFile.delete()
                }
                is Resource.Loading -> {
                    var lista: List<SlikaState> = emptyList()
                    for (i: SlikaState in _state.value.slike) {
                        lista = if (i.slika == slikaState.slika) {
                            lista + SlikaState(isLoading = true, slika = i.slika)
                        } else {
                            lista + i
                        }
                    }
                    _state.value = NovPostState(slike = lista, lokacije = _state.value.lokacije, selected = _state.value.selected)
                    println("Loading " + lista)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun parsePhoto(photo: Bitmap) {
        _state.value = NovPostState(slike = _state.value.slike + SlikaState(slika = photo), lokacije = _state.value.lokacije, selected = _state.value.selected)
    }

    fun givePhotos(): List<SlikaState> {
        return _state.value.slike
    }

    fun deletePhoto(photo: Bitmap) {
        var lista: List<SlikaState> = emptyList()
        for (i: SlikaState in _state.value.slike) {
            if (i.slika != photo) {
                lista = lista + i
                println(i.slika)
                println(photo)
            }
        }
        _state.value = NovPostState(slike = lista, lokacije = _state.value.lokacije, selected = _state.value.selected)
    }

    fun replaceSlikaState(bitmap: Bitmap): List<SlikaState> {
        var lista: List<SlikaState> = emptyList()
        for (i: SlikaState in _state.value.slike) {
            if (i.slika == bitmap)
                lista = lista + SlikaState(isLoading = false, slika = i.slika)
            else
                lista = lista + i
        }
        _state.value = NovPostState(slike = lista, lokacije = _state.value.lokacije, selected = _state.value.selected)
        return lista
    }

    fun ucitajLokacije(ime:String) {
        if (ime == "") {
            getAllLocationsUseCase("Bearer " + access_token).map { result ->
                when (result) {
                    is Resource.Success -> {
                        _state.value = NovPostState(
                            slike = _state.value.slike,
                            lokacije = result.data!!,
                            selected = _state.value.selected
                        )
                        println("Stigle lokacije " + result.data!!)
                    }
                    is Resource.Error -> {
                        println(result.message)
                    }
                    is Resource.Loading -> {
                        println(result.message)
                    }
                }
            }.launchIn(viewModelScope)
        } else {
            getLocationsKeywordUseCase("Bearer " + access_token, ime).map { result ->
                when (result) {
                    is Resource.Success -> {
                        _state.value = NovPostState(
                            slike = _state.value.slike,
                            lokacije = result.data!!,
                            selected = _state.value.selected
                        )
                        println("Stigle lokacije za rec $ime " + result.data!!)
                    }
                    is Resource.Error -> {
                        println(result.message)
                    }
                    is Resource.Loading -> {
                        println(result.message)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

        fun dajLokacije(): List<Location> {
            return _state.value.lokacije
        }

        fun setLocation(id: Long) {
            _state.value = NovPostState(
                lokacije = _state.value.lokacije,
                slike = _state.value.slike,
                selected = id
            )
        }

        fun getLocation(): String {
            for (loc: Location in _state.value.lokacije)
                if (loc.id == _state.value.selected) return loc.name
            return ""
        }

        fun proveriConstants() {
            if (Constants.locationId != 0L) {
                _state.value = NovPostState(
                    slike = _state.value.slike,
                    lokacije = _state.value.lokacije,
                    selected = Constants.locationId
                )
                Constants.locationId = 0L
                ucitajLokacije("")
            } else {
                _state.value = NovPostState(
                    slike = _state.value.slike,
                    lokacije = _state.value.lokacije,
                    selected = _state.value.selected
                )
            }
        }

        fun saveLocation(name: String, position: LatLng, navigator: DestinationsNavigator) {
            Log.d("SAVE LOCATION", "Position is ${position.toString()}")
            saveLocationUseCase(
                "Bearer " + access_token,
                LocationDTO(0, name, position.latitude, position.longitude)
            ).map { result ->
                when (result) {
                    is Resource.Success -> {
                        println(result.data!!)
                        _locationState.value = NovPostMapState(result.data!!)
                        Constants.locationId = result.data!!.id

                        savePost(navigator, description.value, result.data!!.id);
                    }
                    is Resource.Error -> {
                        if (result.message?.contains("403") == true) {
                            GlobalScope.launch(Dispatchers.Main) {
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