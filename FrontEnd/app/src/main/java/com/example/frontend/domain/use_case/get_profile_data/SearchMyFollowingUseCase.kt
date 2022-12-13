package com.example.frontend.domain.use_case.get_profile_data

import com.example.frontend.common.Resource
import com.example.frontend.data.remote.dto.UserDetailedDTO
import com.example.frontend.data.remote.dto.toUser
import com.example.frontend.domain.model.User
import com.example.frontend.domain.repository.CheckpointRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject


class SearchMyFollowingUseCase @Inject constructor(
    private val repository: CheckpointRepository
){
    operator fun invoke(token : String, keyword : String) : Flow<Resource<List<UserDetailedDTO>>> = flow {
        try{
            emit(Resource.Loading())
            val userList = repository.getMyFollowingByUsername(token, keyword)
            emit(Resource.Success(userList))
        }catch (e : HttpException){
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occured"))
        }catch (e : IOException){
            emit(Resource.Error("Couldn't reach server. Please check your internet connection"))
        }
    }
}