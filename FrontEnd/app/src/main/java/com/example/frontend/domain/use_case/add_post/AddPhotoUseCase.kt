package com.example.frontend.domain.use_case.add_post

import com.example.frontend.common.Resource
import com.example.frontend.domain.repository.CheckpointRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class AddPhotoUseCase  @Inject constructor(
    private val repository : CheckpointRepository
) {
    operator fun invoke(token : String,postId: Long, order: Int,photo: MultipartBody.Part) : Flow<Resource<String>> = flow {
        try{
            emit(Resource.Loading())
            val message = repository.addImage(token, postId,order,photo)

            emit(Resource.Success(message.toString()))
        }catch (e : HttpException){
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occured"))
        }catch (e : IOException){
            emit(Resource.Error("Couldn't reach server. Please check your internet connection"))
        }
    }

}