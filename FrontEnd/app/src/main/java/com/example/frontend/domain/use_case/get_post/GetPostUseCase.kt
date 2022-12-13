package com.example.frontend.domain.use_case.get_post

import com.example.frontend.common.Resource
import com.example.frontend.data.remote.dto.toLocation
import com.example.frontend.data.remote.dto.toPost
import com.example.frontend.domain.model.Location
import com.example.frontend.domain.model.Post
import com.example.frontend.domain.repository.CheckpointRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetPostUseCase @Inject constructor(
    private val repository : CheckpointRepository
){

    operator fun invoke(token : String, postId : Long) : Flow<Resource<Post>> = flow {
        try{
            emit(Resource.Loading())
            val post = repository.getPostById(token, postId).toPost()
            emit(Resource.Success(post));
        }catch (e : HttpException){
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occured"))
        }catch (e : IOException){
            emit(Resource.Error("Couldn't reach server. Please check your internet connection"))
        }
    }


}