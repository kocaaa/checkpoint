package com.example.frontend.domain.use_case.login_user

import android.util.Log
import com.example.frontend.common.Resource
import com.example.frontend.data.remote.dto.toLoginToken
import com.example.frontend.domain.model.LoginToken
import com.example.frontend.domain.repository.CheckpointRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject


class LoginUseCase @Inject constructor(
    private val repository: CheckpointRepository
) {

    //only one use case per class to do, and delegate to view model
    operator fun invoke(username : String, password : String) : Flow<Resource<LoginToken>> = flow {
        try {
            emit(Resource.Loading())
            //da je lista, bilo bi .map { it.toLoginToken() }
            Log.d("LOGIN USE CASE", "Loading");
            val token = repository.login(username, password).toLoginToken()
            Log.d("LOGIN USE CASE", "Token *${token.toString()}*");
            emit(Resource.Success(token))

        }catch (e : HttpException){
            Log.d("LOGIN USE CASE", "HTTP error ${e.message}");
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occured"))
        }catch (e : IOException){
            Log.d("LOGIN USE CASE", "IO error ${e.message}");
            emit(Resource.Error("Couldn't reach server. Please check your internet connection"))
        }
    }

}