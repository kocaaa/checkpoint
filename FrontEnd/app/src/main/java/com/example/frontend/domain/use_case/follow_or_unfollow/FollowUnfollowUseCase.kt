package com.example.frontend.domain.use_case.follow_or_unfollow

import com.example.frontend.common.Resource
import com.example.frontend.domain.repository.CheckpointRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject


class FollowUnfollowUseCase @Inject constructor(
    private val repository : CheckpointRepository
){

    operator fun invoke(token : String, userId : Long) : Flow<Resource<String>> = flow {
        try{
            emit(Resource.Loading())
            val response = repository.followOrUnfollowUser(token, userId)
            emit(Resource.Success(response));
        }catch (e : HttpException){
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occured"))
        }catch (e : IOException){
            emit(Resource.Error("Couldn't reach server. Please check your internet connection"))
        }
    }


}