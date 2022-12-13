package com.example.frontend.domain.use_case.get_posts

import com.example.frontend.common.Resource
import com.example.frontend.data.remote.dto.toPost
import com.example.frontend.domain.model.Post
import com.example.frontend.domain.repository.CheckpointRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetPostsIFollowUseCase @Inject constructor(
    private val repository: CheckpointRepository
) {

    operator fun invoke(token: String ) : Flow<Resource<List<Post>>> = flow {
        try{
            emit(Resource.Loading())
            val posts = repository.getPostsOfUsersThatIFollow(token).map{
                it.toPost()
            }
            emit(Resource.Success(posts))
        }catch (e : HttpException){
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occured"))
        }catch (e : IOException){
            emit(Resource.Error("Couldn't reach server. Please check your internet connection"))
        }
    }
}