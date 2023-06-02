package com.academy.bangkit.mystoryapp.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.academy.bangkit.mystoryapp.data.Result
import com.academy.bangkit.mystoryapp.data.local.datastore.UserPreferences
import com.academy.bangkit.mystoryapp.data.local.entity.StoryEntity
import com.academy.bangkit.mystoryapp.data.local.room.StoryDatabase
import com.academy.bangkit.mystoryapp.data.network.retrofit.StoryApiService
import com.academy.bangkit.mystoryapp.data.local.paging.StoryRemoteMediator
import com.academy.bangkit.mystoryapp.data.network.response.CommonResponse
import com.academy.bangkit.mystoryapp.data.network.response.LoginResponse
import com.academy.bangkit.mystoryapp.data.network.response.Story
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class StoryRepository(
    private val storyDatabase: StoryDatabase,
    private val apiService: StoryApiService,
    private val userPreferences: UserPreferences
) {

    fun signup(name: String, email: String, password: String): Flow<Result<CommonResponse>> =
        flow {
            emit(Result.Loading)
            try {
                val response = apiService.signup(name, email, password)

                if (response.error) {
                    emit(Result.Error(response.message))
                } else {
                    emit(Result.Success(response))
                }

            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }.flowOn(Dispatchers.IO)

    fun login(email: String, password: String): Flow<Result<LoginResponse>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.login(email, password)
            userPreferences.run {
                saveToken(response.loginResult?.token.toString())
                setLogin(true)
            }

            if (response.error) {
                emit(Result.Error(response.message))
            } else {
                emit(Result.Success(response))
            }

        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)


    fun getStories(): Flow<PagingData<StoryEntity>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, userPreferences),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStories()
            }
        ).flow
    }

    fun getStoriesWithLocation(): Flow<Result<List<Story>>> = flow {
        emit(Result.Loading)
        try {
            val token = userPreferences.getToken().first()
            val response = apiService.getAllStories("Bearer $token", location = 1)

            if (response.error) {
                emit(Result.Error(response.message))
            } else {
                emit(Result.Success(response.listStory))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)

    fun addNewStory(
        token: String,
        photo: File,
        description: String,
        lat: Float?,
        lon: Float?
    ): Flow<Result<CommonResponse>> = flow {
        emit(Result.Loading)

        val textPlainMediaType = "text/plain".toMediaType()
        val imageMediaType = "image/jpeg".toMediaType()

        val imageMultiPart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            photo.name,
            photo.asRequestBody(imageMediaType)
        )

        val descriptionRequestBody = description.toRequestBody(textPlainMediaType)
        val latRequestBody = lat.toString().toRequestBody(textPlainMediaType)
        val lonRequestBody = lon.toString().toRequestBody(textPlainMediaType)

        try {
            val token = userPreferences.getToken().first()
            val response =
                apiService.addNewStory(
                    "Bearer $token",
                    imageMultiPart,
                    descriptionRequestBody,
                    latRequestBody,
                    lonRequestBody
                )

            if (response.error) {
                emit(Result.Error(response.message))
            } else {
                emit(Result.Success(response))
            }

        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)

    fun getLogin(): Flow<Boolean> = userPreferences.getLogin()

    suspend fun deleteCredential() {
        userPreferences.run {
            destroyToken()
            setLogin(false)
        }
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(
            storyDatabase: StoryDatabase,
            apiService: StoryApiService,
            userPreferences: UserPreferences,
        ): StoryRepository = instance ?: synchronized(this) {
            instance ?: StoryRepository(storyDatabase, apiService, userPreferences)
        }.also { instance = it }
    }
}