package com.example.frontend.domain.use_case.refresh_page

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class RefreshPageUseCase @Inject constructor(

) {

    operator fun invoke() : Flow<Boolean> = flow {
        emit(true)
        delay(1000)
        emit(false)
    }

}