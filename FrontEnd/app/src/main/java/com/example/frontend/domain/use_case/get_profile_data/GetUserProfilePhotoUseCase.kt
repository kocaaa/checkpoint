package com.example.frontend.domain.use_case.get_profile_data

import android.util.Log
import com.example.frontend.common.Resource
import com.example.frontend.domain.model.ProfileData
import com.example.frontend.domain.repository.CheckpointRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetUserProfilePhotoUseCase @Inject constructor(
    private val repository: CheckpointRepository
){
    operator fun invoke(token:String, userId : Long) : Flow<Resource<String>> = flow{
        try{
            emit(Resource.Loading())
            Log.d("PHOTO", "GETTING");
            val photo = repository.getUserProfilePicture(token, userId);
            Log.d("PHOTO", "GOT PHOTO ${photo.toString()}");
            emit(Resource.Success(photo));
        }catch (e : HttpException){
            Log.d("ERROR", e.localizedMessage);
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occured"))
        }catch (e : IOException){
            Log.d("ERROR", e.localizedMessage);
            emit(Resource.Error("Couldn't reach server. Please check your internet connection"))
        }
    }
}