package com.example.frontend.domain.use_case.post_comments

import com.example.frontend.common.Resource
import com.example.frontend.domain.model.Comment
import com.example.frontend.domain.repository.CheckpointRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetFirstCommentsUseCase @Inject constructor(
    private val repository: CheckpointRepository
) {
    operator fun invoke(token : String, postId: Long) : Flow<Resource<List<Comment>>> = flow {
        try{
            emit(Resource.Loading())
            val comments = repository.getFirstCommentsByPostId(token, postId).map{
                it
            };
            emit(Resource.Success(comments));
        }catch (e : HttpException){
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occured"))
        }catch (e : IOException){
            emit(Resource.Error("Couldn't reach server. Please check your internet connection"))
        }
    }
}