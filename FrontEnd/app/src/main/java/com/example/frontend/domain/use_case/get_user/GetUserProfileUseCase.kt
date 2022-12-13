package com.example.frontend.domain.use_case.get_user

import com.example.frontend.common.Resource
import com.example.frontend.domain.model.User
import com.example.frontend.domain.repository.CheckpointRepository
import kotlinx.coroutines.flow.flow
import java.util.concurrent.Flow
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val repository: CheckpointRepository
){
//    operator fun invoke(token:String, userId : Long) : Flow<Resource<List<User>>> = flow{
//        try{
//            emit(Resource.Loading())
////            val user = repository.getUser().toU
//            emit(Resource.Success(user));
//        }
//    }
}