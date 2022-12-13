package com.example.frontend.presentation.ui

import android.app.Application
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.common.Resource
import com.example.frontend.domain.DataStoreManager
import com.example.frontend.domain.use_case.login_user.GetLoginUserIdUseCase
import com.example.frontend.domain.use_case.session.SessionUseCase
import com.example.frontend.presentation.login.components.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val sessionUseCase : SessionUseCase,
    private val getLoginUserIdUseCase: GetLoginUserIdUseCase,
    private var application: Application
) : ViewModel() {

    private val _authState = mutableStateOf(AuthState())
    val authState : State<AuthState> = _authState
    val context = application.baseContext

    var access_token  = "";
    var refresh_token = "";

    init{

    }

    private fun authUser(){
        sessionUseCase("Bearer "+refresh_token).onEach { result ->
            when(result){
                is Resource.Success -> {
                    Log.d("AUTH SUCCESS", "Success");
                    GlobalScope.launch(Dispatchers.Main) {
                        DataStoreManager.saveValue(context, "access_token", result.data!!.access_token);
                        DataStoreManager.saveValue(context, "refresh_token", result.data!!.refresh_token);

                        val jwtDecode = DataStoreManager.decodeToken(result.data!!.access_token);

                        val username = JSONObject(jwtDecode).getString("sub")
                        DataStoreManager.saveValue(context, "username", username)

                        saveUserId(result.data!!.access_token);

                        _authState.value = AuthState(isAuthorized = true);
                    }

                }
                is Resource.Error -> {
                    _authState.value = AuthState(error = result.message ?:
                    "An unexpected error occured")
                }
                is Resource.Loading -> {
                    _authState.value = AuthState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun saveUserId(access_token: String){
        getLoginUserIdUseCase("Bearer "+access_token).onEach { result ->
            when(result){
                is Resource.Success -> {
                    val userId = result.data
                    Log.d("User id", "Fetched user id ${userId}")
                    if (userId != null) {
                        DataStoreManager.saveValue(context, "userId", userId.toInt())
                    }
                }
                is Resource.Error -> {
                    DataStoreManager.saveValue(context, "userId", 0)
                }
                is Resource.Loading -> {
                    DataStoreManager.saveValue(context, "userId", 0)
                }
            }
        }.launchIn(viewModelScope)
    }


}