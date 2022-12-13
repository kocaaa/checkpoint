package com.example.frontend.presentation.user_list

import Constants.Companion.USER_ID
import Constants.Companion.USER_LIST_TYPE
import android.app.Application
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.common.Resource
import com.example.frontend.domain.DataStoreManager
import com.example.frontend.domain.use_case.get_profile_data.*
import com.example.frontend.domain.use_case.refresh_page.RefreshPageUseCase
import com.example.frontend.presentation.location.components.LocationState
import com.example.frontend.presentation.user_list.components.UserListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserListViewModel @Inject constructor(
    private val getUserFollowersUseCase: GetUserFollowersUseCase,
    private val getUserFollowingUseCase: GetUserFollowingUseCase,
    private val searchUserFollowersUseCase: SearchUserFollowersUseCase,
    private val searchUserFollowingUseCase: SearchUserFollowingUseCase,
    private val refreshPageUseCase : RefreshPageUseCase,
    private val savedStateHandle: SavedStateHandle,
    application: Application
) : ViewModel(){

    private val _state = mutableStateOf(UserListState())
    val state : State<UserListState> = _state
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing : StateFlow<Boolean> get() = _isRefreshing.asStateFlow()

    private val context = application.baseContext
    private var access_token = "";
    private var refresh_token = "";
    var username = "";
    var loginUserId = 0L;

    var typeOfUsers = "";
    var savedUserId : Long = 0L;

    init {
        GlobalScope.launch(Dispatchers.Main){
            access_token =  DataStoreManager.getStringValue(context, "access_token").trim();
            refresh_token = DataStoreManager.getStringValue(context, "refresh_token").trim();
            username = DataStoreManager.getStringValue(context, "username");
            loginUserId = DataStoreManager.getLongValue(context, "userId");

            savedStateHandle.get<Long>(USER_ID)?.let { userId ->
                savedUserId = userId
            }

            savedStateHandle.get<String>(USER_LIST_TYPE)?.let { userTypeList ->
                if(userTypeList == "following") {
                    typeOfUsers = "Following"
                }
                else if(userTypeList == "followers") {
                    typeOfUsers = "Followers"
                }
            }

            getAllUsers()
        }
    }


    fun getAllUsers()
    {
        if(typeOfUsers == "Following"){
            getUsersFollowing(access_token, savedUserId);
        }
        else if(typeOfUsers == "Followers"){
            getUsersFollowers(access_token, savedUserId);
        }
    }



    fun searchUsers(keyword: String)
    {
        if(typeOfUsers == "Following"){
            searchUsersFollowing(access_token, savedUserId, keyword);
        }
        else if(typeOfUsers == "Followers"){
            searchUsersFollowers(access_token, savedUserId, keyword);
        }
    }


    private fun getUsersFollowers(token: String, userId : Long){
        getUserFollowersUseCase("Bearer "+token, userId).onEach { result ->
            when(result){
                is Resource.Success -> {
                    _state.value = UserListState(users = result.data ?: emptyList())
                }
                is Resource.Error -> {
                    _state.value = UserListState(error = result.message ?:
                    "An unexpected error occured")
                    if(result.message?.contains("403") == true){
                        GlobalScope.launch(Dispatchers.Main){
                            DataStoreManager.deleteAllPreferences(context);
                        }
                    }
                }
                is Resource.Loading -> {
                    _state.value = UserListState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun getUsersFollowing(token: String, userId : Long){
        getUserFollowingUseCase("Bearer "+token, userId).onEach { result ->
            when(result){
                is Resource.Success -> {
                    _state.value = UserListState(users = result.data ?: emptyList())
                }
                is Resource.Error -> {
                    _state.value = UserListState(error = result.message ?:
                    "An unexpected error occured")
                    if(result.message?.contains("403") == true){
                        GlobalScope.launch(Dispatchers.Main){
                            DataStoreManager.deleteAllPreferences(context);
                        }
                    }
                }
                is Resource.Loading -> {
                    _state.value = UserListState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun searchUsersFollowing(token : String, userId : Long, keyword: String){
        searchUserFollowingUseCase("Bearer "+token, userId, keyword).onEach { result ->
            when(result){
                is Resource.Success -> {
                    _state.value = UserListState(users = result.data ?: emptyList())
                }
                is Resource.Error -> {
                    _state.value = UserListState(error = result.message ?:
                    "An unexpected error occured")
                    if(result.message?.contains("403") == true){
                        GlobalScope.launch(Dispatchers.Main){
                            DataStoreManager.deleteAllPreferences(context);
                        }
                    }
                }
                is Resource.Loading -> {
                    _state.value = UserListState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun searchUsersFollowers(token : String, userId : Long, keyword: String){
        searchUserFollowersUseCase("Bearer "+token, userId, keyword).onEach { result ->
            when(result){
                is Resource.Success -> {
                    _state.value = UserListState(users = result.data ?: emptyList())
                }
                is Resource.Error -> {
                    _state.value = UserListState(error = result.message ?:
                    "An unexpected error occured")
                    if(result.message?.contains("403") == true){
                        GlobalScope.launch(Dispatchers.Main){
                            DataStoreManager.deleteAllPreferences(context);
                        }
                    }
                }
                is Resource.Loading -> {
                    _state.value = UserListState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun refreshPage(){
        refreshPageUseCase().onEach{ result ->
            _isRefreshing.emit(result)
        }.launchIn(viewModelScope)
    }

    fun proveriConstants(){
        if(Constants.refreshPhotoConstant != 0L || (this.loginUserId == this.savedUserId && Constants.refreshFollowUnfollowConstant != 0L)){
            Constants.refreshPhotoConstant = 0L
            Constants.refreshFollowUnfollowConstant = 0L
            getAllUsers()
        }
    }

}