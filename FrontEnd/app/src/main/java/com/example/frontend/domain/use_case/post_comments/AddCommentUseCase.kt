package com.example.frontend.domain.use_case.post_comments

import com.example.frontend.common.Resource
import com.example.frontend.domain.model.Comment
import com.example.frontend.domain.repository.CheckpointRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class AddCommentUseCase @Inject constructor(
    private val repository: CheckpointRepository
) {
    operator fun invoke(token : String, commentText: String, postId: Long, parentCommentId: Long) : Flow<Resource<String>> = flow {
        try{
            emit(Resource.Loading())
            val message = repository.addComment(token, commentText, postId, parentCommentId)
            emit(Resource.Success(message))
        }catch (e : HttpException){
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occured"))
        }catch (e : IOException){
            emit(Resource.Error("Couldn't reach server. Please check your internet connection"))
        }
    }
}