package com.example.frontend.domain.use_case.get_profile_data

import com.example.frontend.common.Resource
import com.example.frontend.domain.DataStoreManager
import com.example.frontend.domain.model.ProfileData
import com.example.frontend.domain.repository.CheckpointRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetUserProfileDataUseCase @Inject constructor(
    private val repository: CheckpointRepository
){
    operator fun invoke(token:String, userId : Long, loginUserId : Long) : Flow<Resource<ProfileData>> = flow{
        try{
            emit(Resource.Loading())
            val username = "username korisnika";
            val followersCount = repository.countAllFollowersPerUser(token, userId)
            val followingCount = repository.countAllFollowingByUser(token, userId)
            val postCount = repository.getUserPostsCount(token, userId)

            var amFollowing = false;
            if(userId != loginUserId)
            {
                repository.getMyFollowing(token).forEach { following ->
                    if(following.id == userId)
                        amFollowing = true;
                    return@forEach;
                }
            }


            val profileData = ProfileData(username,followersCount, followingCount, postCount, amFollowing)
            emit(Resource.Success(profileData));
        }catch (e : HttpException){
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occured"))
        }catch (e : IOException){
            emit(Resource.Error("Couldn't reach server. Please check your internet connection"))
        }
    }
}