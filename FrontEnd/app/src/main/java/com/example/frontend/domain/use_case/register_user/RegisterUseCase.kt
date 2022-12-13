package com.example.frontend.domain.use_case.register_user

import com.example.frontend.common.Resource
import com.example.frontend.data.remote.dto.toLoginToken
import com.example.frontend.domain.model.LoginToken
import com.example.frontend.domain.model.RegisterUser
import com.example.frontend.domain.repository.CheckpointRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject


class RegisterUseCase @Inject constructor(
    private val repository: CheckpointRepository
) {

    operator fun invoke(user : RegisterUser) : Flow<Resource<String>> = flow {
        try {
            emit(Resource.Loading())
            val message = repository.register(user)
            emit(Resource.Success(message))

        }catch (e : HttpException){
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occured"))
        }catch (e : IOException){
            emit(Resource.Error("Couldn't reach server. Please check your internet connection"))
        }
    }

}