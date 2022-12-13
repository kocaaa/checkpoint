package com.example.frontend.domain.use_case.session

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

class SessionUseCase @Inject constructor(
    private val repository : CheckpointRepository
){
    operator fun invoke(token : String) : Flow<Resource<LoginToken>> = flow {
        try{
            emit(Resource.Loading())
            Log.d("SESSION", "Getting");
            val auth = repository.authorizeUser(token).toLoginToken();
            Log.d("SESSTON", "Got ${auth.toString()}");
            emit(Resource.Success(auth))
        }catch (e : HttpException){
            e.message?.let { Log.d("SESSION HTTP", it) };
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occured"))
        }catch (e : IOException){
            e.message?.let { Log.d("SESSION IO", it) };
            emit(Resource.Error("Couldn't reach server. Please check your internet connection"))
        }
    }
}
