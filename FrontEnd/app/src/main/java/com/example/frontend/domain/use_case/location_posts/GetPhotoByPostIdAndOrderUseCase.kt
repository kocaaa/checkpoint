package com.example.frontend.domain.use_case.location_posts

import com.example.frontend.common.Resource
import com.example.frontend.data.remote.dto.BinaryPhoto
import com.example.frontend.domain.repository.CheckpointRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetPhotoByPostIdAndOrderUseCase @Inject constructor(
    private val repository: CheckpointRepository
) {

    operator fun invoke(token: String, postId:Long, order: Int ) : Flow<Resource<BinaryPhoto>> = flow {
        try{
            emit(Resource.Loading())
            val posts = repository.getPhotoByPostIdAndOrder(token, postId, order)
            emit(Resource.Success(posts))
        }catch (e : HttpException){
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occured"))
        }catch (e : IOException){
            emit(Resource.Error("Couldn't reach server. Please check your internet connection"))
        }
    }
}