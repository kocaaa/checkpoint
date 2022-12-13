package com.example.frontend.domain.use_case.get_locations

import com.example.frontend.common.Resource
import com.example.frontend.data.remote.dto.LocationDTO
import com.example.frontend.data.remote.dto.toLocation
import com.example.frontend.domain.model.Location
import com.example.frontend.domain.repository.CheckpointRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class SaveLocationUseCase @Inject constructor(
    private val repository : CheckpointRepository
) {

    operator fun invoke(token : String, location : LocationDTO) : Flow<Resource<LocationDTO>> = flow {
        try{
            emit(Resource.Loading())
            val locations = repository.saveLocation(token, location)
            emit(Resource.Success(locations));
        }catch (e : HttpException){
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occured"))
        }catch (e : IOException){
            emit(Resource.Error("Couldn't reach server. Please check your internet connection"))
        }
    }

}