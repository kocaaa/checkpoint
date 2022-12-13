package com.example.frontend.domain.use_case.get_profile_data

import com.example.frontend.common.Resource
import com.example.frontend.data.remote.dto.toUser
import com.example.frontend.domain.model.ProfileData
import com.example.frontend.domain.repository.CheckpointRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetMyProfileDataUseCase @Inject constructor(
    private val repository: CheckpointRepository
){
    operator fun invoke(token:String) : Flow<Resource<ProfileData>> = flow{
        try{
            emit(Resource.Loading())
            val followersCount = repository.getMyFollowersCount(token)
            val followingCount = repository.getMyFollowingCount(token)
            val postCount = repository.getMyPostsCount(token)

            val profileData = ProfileData("",followersCount, followingCount, postCount, false)
            emit(Resource.Success(profileData));
        }catch (e : HttpException){
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occured"))
        }catch (e : IOException){
            emit(Resource.Error("Couldn't reach server. Please check your internet connection"))
        }
    }
}