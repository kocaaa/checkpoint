package com.example.frontend.di

import Constants.Companion.BASE_URL
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import com.example.frontend.data.remote.CheckpointApi
import com.example.frontend.data.repository.CheckpointRepositoryImpl
import com.example.frontend.domain.repository.CheckpointRepository
import com.example.frontend.presentation.NavGraphs
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideCheckpointApi() : CheckpointApi {
        var client : OkHttpClient = OkHttpClient().newBuilder().readTimeout(120,TimeUnit.SECONDS).writeTimeout(120,TimeUnit.SECONDS).build()
        return Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(
                Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            ))
            .build()
            .create(CheckpointApi::class.java)
    }


    @Provides
    @Singleton
    fun provideCheckpointRepository(api : CheckpointApi) : CheckpointRepository {
        return CheckpointRepositoryImpl(api)
    }

}