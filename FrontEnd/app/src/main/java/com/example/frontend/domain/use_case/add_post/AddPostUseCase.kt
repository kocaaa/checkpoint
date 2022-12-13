package com.example.frontend.domain.use_case.add_post

import com.example.frontend.common.Resource
import com.example.frontend.domain.repository.CheckpointRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class AddPostUseCase @Inject constructor(
    private val repository : CheckpointRepository
) {
    operator fun invoke(token : String,description: String, locationId: Long) : Flow<Resource<String>> = flow {
        try{
            emit(Resource.Loading())
            val message = repository.savePost(token, description,locationId)
            emit(Resource.Success(message.toString()))
        }catch (e : HttpException){
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occured"))
        }catch (e : IOException){
            emit(Resource.Error("Couldn't reach server. Please check your internet connection"))
        }
    }

}