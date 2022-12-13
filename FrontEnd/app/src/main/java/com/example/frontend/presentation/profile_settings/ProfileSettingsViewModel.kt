package com.example.frontend.presentation.profile_settings

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import android.util.Patterns
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavHost
import com.example.frontend.common.Resource
import com.example.frontend.domain.DataStoreManager
import com.example.frontend.domain.use_case.get_user.GetMyProfilePictureUseCase
import com.example.frontend.domain.use_case.get_user.GetUserInfoUseCase
import com.example.frontend.domain.use_case.profile_settings.ChangeEmailUseCase
import com.example.frontend.domain.use_case.profile_settings.ChangePasswordUseCase
import com.example.frontend.domain.use_case.profile_settings.ChangeProfilePictureUseCase
import com.example.frontend.presentation.destinations.LoginScreenDestination
import com.example.frontend.presentation.destinations.MainLocationScreenDestination
import com.example.frontend.presentation.destinations.ProfileSettingsScreenDestination
import com.example.frontend.presentation.destinations.RegisterScreenDestination
import com.example.frontend.presentation.profile_settings.components.ChangeProfilePictureState
import com.example.frontend.presentation.profile_settings.components.ProfilePictureState
import com.example.frontend.presentation.profile_settings.components.UserInfoChangeState
import com.example.frontend.presentation.profile_settings.components.ProfileSettingsUserState
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class ProfileSettingsViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getMyProfilePictureUseCase: GetMyProfilePictureUseCase,
    private val changeEmailUseCase: ChangeEmailUseCase,
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val changeProfilePictureUseCase: ChangeProfilePictureUseCase,
    savedStateHandle: SavedStateHandle,
    application: Application
): ViewModel() {

    private val _state = mutableStateOf(ProfileSettingsUserState())
    val state : State<ProfileSettingsUserState> = _state
    val context = application.baseContext
    
    private val _stateGetMyProfilePicture = mutableStateOf(ProfilePictureState())
    val stateGetMyProfilePicture : State<ProfilePictureState> = _stateGetMyProfilePicture

    private val _stateEmailChange = mutableStateOf(UserInfoChangeState())
    val stateEmailChange : State<UserInfoChangeState> = _stateEmailChange
    var currentEmail: String = "";

    private val _stateChangeProfilePicture = mutableStateOf(ChangeProfilePictureState())
    val stateChangeProfilePicture : State<ChangeProfilePictureState> = _stateChangeProfilePicture
    var changePictureEnabled: Boolean = false;
    var currentPicture: String = "";
    //var flagPictureFirstShow = true;
    var imgbitmap: Bitmap? = null;

    private val _statePasswordChange = mutableStateOf(UserInfoChangeState())
    val statePasswordChange : State<UserInfoChangeState> = _statePasswordChange
    var currentOldPassInputValue: String = ""
    var currentNewPass1InputValue: String = ""
    var currentNewPass2InputValue: String = ""

    var access_token  = "";
    var refresh_token = "";
    var username = "";
    var loginUserId = 0L;

    init {
        GlobalScope.launch(Dispatchers.Main){
            access_token =  DataStoreManager.getStringValue(context, "access_token").trim();
            refresh_token = DataStoreManager.getStringValue(context, "refresh_token").trim();
            loginUserId = DataStoreManager.getLongValue(context, "userId");

            //Constants.refreshPhotoConstant = loginUserId
            getMyData()
            getMyProfilePicture()
        }
    }

    fun getMyData()
    {
        GlobalScope.launch(Dispatchers.Main){

            getUserInfoUseCase("Bearer "+ access_token).onEach { result ->
                when(result){
                    is Resource.Success -> {
                        _state.value = ProfileSettingsUserState(user = result.data)
                        println("DAJ PODATKE" + result.data)
                        currentEmail = result.data!!.email
                    }
                    is Resource.Error -> {
                        _state.value = ProfileSettingsUserState(error = result.message ?:"An unexpected error occured")

                        if(result.message?.contains("403") == true){
                            GlobalScope.launch(Dispatchers.Main){
                                DataStoreManager.deleteAllPreferences(context);
                            }
                        }
                    }
                    is Resource.Loading -> {
                        _state.value = ProfileSettingsUserState(isLoading = true)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun getMyProfilePicture()
    {
        GlobalScope.launch(Dispatchers.Main){

            getMyProfilePictureUseCase("Bearer "+ access_token).onEach { result ->
                when(result){
                    is Resource.Success -> {
                        println("****////////********GETMYPROFILEPICTURE SUCCESS ")
                        _stateGetMyProfilePicture.value = ProfilePictureState(picture = result.data!!)
                        currentPicture = result.data!!
                    }
                    is Resource.Error -> {
                        println("GETMYPROFILEPICTURE ERROR" + result.message)
                        _stateGetMyProfilePicture.value = ProfilePictureState(error = result.message ?:"An unexpected error occured")

                        if(result.message?.contains("403") == true){
                            GlobalScope.launch(Dispatchers.Main){
                                DataStoreManager.deleteAllPreferences(context);
                            }
                        }
                    }
                    is Resource.Loading -> {
                        _stateGetMyProfilePicture.value = ProfilePictureState(isLoading = true)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun changeProfilePicture(navigator : DestinationsNavigator)
    {
        GlobalScope.launch(Dispatchers.IO){

            Constants.refreshPhotoConstant = loginUserId

            var access_token = DataStoreManager.getStringValue(context, "access_token")
            var refresh_token = DataStoreManager.getStringValue(context, "refresh_token")
            val path = context.getExternalFilesDir(null)!!.absolutePath
            val tempFile = File(path, "tempFileName.jpg")
            val fOut = FileOutputStream(tempFile)
            stateChangeProfilePicture.value.picture!!.compress(Bitmap.CompressFormat.JPEG, 85, fOut)
            fOut.flush()
            fOut.close()
            val file = MultipartBody.Part.createFormData(
                "profile_image", tempFile.name,
                RequestBody.create(MediaType.parse("image/*"), tempFile)
            )

            changeProfilePictureUseCase("Bearer "+ access_token, file).onEach { result ->
                when(result){
                    is Resource.Success -> {
                        println("****////////********CHANGEMYPROFILEPICTURE SUCCESS ")
                        tempFile.delete()
                        //navigator.navigate(ProfileSettingsScreenDestination())
                        changePictureEnabled = true;
                        println(result.data)
                    }
                    is Resource.Error -> {
                        if(result.message?.contains("403") == true){
                            GlobalScope.launch(Dispatchers.Main){
                                DataStoreManager.deleteAllPreferences(context);
                            }
                        }

                        println("CHANGEMYPROFILEPICTURE ERROR" + result.message)
                        _stateChangeProfilePicture.value = ChangeProfilePictureState(error = result.message ?:"An unexpected error occured")
                        tempFile.delete()
                    }
                    is Resource.Loading -> {
                        _stateChangeProfilePicture.value = ChangeProfilePictureState(isLoading = true)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun parsePhoto(photo: Bitmap) {
        _stateChangeProfilePicture.value = ChangeProfilePictureState(picture = photo)
        changePictureEnabled = true;
    }

    fun changeEmail(navigator: DestinationsNavigator)
    {
        if(!Patterns.EMAIL_ADDRESS.matcher(currentEmail).matches()){
            _stateEmailChange.value = UserInfoChangeState(error = "Email not valid")
            return
        }
        GlobalScope.launch(Dispatchers.Main){
            changeEmailUseCase("Bearer "+ access_token, currentEmail).onEach { result ->
                when(result){
                    is Resource.Success -> {
                        _stateEmailChange.value = UserInfoChangeState(message = result.data ?: "")
                    }
                    is Resource.Error -> {
                        if(result.message?.contains("403") == true){
                            GlobalScope.launch(Dispatchers.Main){
                                DataStoreManager.deleteAllPreferences(context);
                            }
                        }
                        _stateEmailChange.value = UserInfoChangeState(error = result.message ?:"An unexpected error occured")
                    }
                    is Resource.Loading -> {
                        _stateEmailChange.value = UserInfoChangeState(isLoading = true)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun changePassword()
    {
        if (currentOldPassInputValue == "" || currentNewPass1InputValue == "" || currentNewPass2InputValue == "")
            return;
        if (currentNewPass1InputValue.length < 7){
            _statePasswordChange.value = UserInfoChangeState(error = "Password needs to be at least 7 charactes")
            return
        }
        if (currentNewPass1InputValue != currentNewPass2InputValue)
        {
            _statePasswordChange.value = UserInfoChangeState(error = "Passwords are not matching")
            return
        }
        if(currentOldPassInputValue == currentNewPass1InputValue){
            _statePasswordChange.value = UserInfoChangeState(error = "New password cant be same as old password")
            return
        }


        val passwordArray =  arrayOf(currentOldPassInputValue, currentNewPass1InputValue);

        GlobalScope.launch(Dispatchers.Main){
            changePasswordUseCase("Bearer "+ access_token, passwordArray).onEach { result ->
                when(result){
                    is Resource.Success -> {
                        _statePasswordChange.value = UserInfoChangeState(message = result.data ?: "")
                        currentOldPassInputValue = ""
                        currentNewPass1InputValue = ""
                        currentNewPass2InputValue = ""
                    }
                    is Resource.Error -> {
                        println(result.message!!)
                        println(result.data!!)
                        if(result.message?.contains("403") == true){
                            GlobalScope.launch(Dispatchers.Main){
                                DataStoreManager.deleteAllPreferences(context);
                            }
                        }
                        _statePasswordChange.value = UserInfoChangeState(error = result.message ?:"An unexpected error occured")
                    }
                    is Resource.Loading -> {
                        _statePasswordChange.value = UserInfoChangeState(isLoading = true)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun logoutUser(navigator : DestinationsNavigator){
        Log.d("LOGOUT", "Logout user");
        GlobalScope.launch(Dispatchers.Main){
            DataStoreManager.deleteAllPreferences(context)

            Log.d("LOGOUT", "Cleared preferences");
            Log.d("Navigator", navigator.toString());

            navigator.navigate(LoginScreenDestination()){
                popUpTo(ProfileSettingsScreenDestination.route){
                    inclusive = true
                }
            }

            Log.d("Navigator", navigator.toString());
            Log.d("LOGOUT", "Logged out user");

        }
    }
}