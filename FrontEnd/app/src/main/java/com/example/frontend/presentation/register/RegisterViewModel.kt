package com.example.frontend.presentation.register

import android.content.Context
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.frontend.common.Resource
import com.example.frontend.domain.model.RegisterUser
import com.example.frontend.domain.use_case.register_user.RegisterUseCase
import com.example.frontend.presentation.destinations.LoginScreenDestination
import com.example.frontend.presentation.destinations.MainFeedScreenDestination
import com.example.frontend.presentation.register.components.RegisterState
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
) : ViewModel(){


    private val _state = mutableStateOf(RegisterState())
    val state : State<RegisterState> = _state

    private val _stateValidation = mutableStateOf(RegisterState())
    val stateValidation : State<RegisterState> = _stateValidation


    fun register(mailNotTrimmed:String , usernameNotTrimmed:String, password:String,
                 passwordRepeat:String, navigator : DestinationsNavigator, context : Context
    )
    {
        Log.d("REGISTER", "Username ${usernameNotTrimmed}, mail ${mailNotTrimmed}, password ${password}, passwordRepeat ${passwordRepeat}");

        var username = usernameNotTrimmed.trim()
        var mail = mailNotTrimmed.trim()

        if(username.length == 0){
            _stateValidation.value = RegisterState(error = "Username cant be empty");
        }
        else if(password.length<7){
            println("kratka sifra")
            _stateValidation.value = RegisterState(error = "Password must be at least 7 characters");
        }
        else if(password != passwordRepeat){
            println("sifre nisu iste")
            _stateValidation.value = RegisterState(error = "Passwords don't match!");
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(mail).matches()){
            println("email nije dobar")
            _stateValidation.value = RegisterState(error = "Email not valid");
        }
        else{
            _stateValidation.value = RegisterState(error = "");
            var user = RegisterUser(mail, username, password );
            registerUseCase(user).onEach { result ->
                when(result){
                    is Resource.Success -> {
                        result.data?.let { Log.d("REGISTER", it) };
                        if(result.data == "Successfully registered.")
                        {
                            _state.value = RegisterState(message = result.data ?: "")

                            navigator.navigate(
                                LoginScreenDestination()
                            )
                        }
                        else{
                            _state.value = RegisterState(error = result.data ?: "")

                            Toast.makeText(
                                context,
                                result.data,
                                Toast.LENGTH_LONG
                            ).show();
                        }
                    }
                    is Resource.Error -> {
                        Log.d("Error", "Error is ${result.message}");
                        _state.value = RegisterState(error = result.message ?:
                        "An unexpected error occured")

                        Toast.makeText(
                            context,
                            result.message,
                            Toast.LENGTH_LONG
                        ).show();
                    }
                    is Resource.Loading -> {
                        _state.value = RegisterState(isLoading = true)
                    }
                }
            }.launchIn(viewModelScope)
        }

        if(_stateValidation.value.error.isNotEmpty()){
            Toast.makeText(
                context,
                _stateValidation.value.error,
                Toast.LENGTH_LONG
            ).show();
        }
    }

}