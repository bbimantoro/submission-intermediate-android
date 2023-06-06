package com.academy.bangkit.mystoryapp.data.repository

import android.location.Location
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.academy.bangkit.mystoryapp.data.Result
import com.academy.bangkit.mystoryapp.data.local.datastore.UserPreferences
import com.academy.bangkit.mystoryapp.data.local.entity.StoryEntity
import com.academy.bangkit.mystoryapp.data.local.paging.StoryRemoteMediator
import com.academy.bangkit.mystoryapp.data.local.room.StoryDatabase
import com.academy.bangkit.mystoryapp.data.network.response.CommonResponse
import com.academy.bangkit.mystoryapp.data.network.response.LoginResponse
import com.academy.bangkit.mystoryapp.data.network.retrofit.StoryApiService
import com.academy.bangkit.mystoryapp.utils.DataMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

private const val PAGE_SIZE_ITEM = 10

class StoryRepository(
    private val storyDatabase: StoryDatabase,
    private val apiService: StoryApiService,
    private val userPreferences: UserPreferences
) {
    fun getStories(): Flow<PagingData<StoryEntity>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE_ITEM
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, userPreferences),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStories()
            }
        ).flow
    }

    fun getStoriesWithLocation(): Flow<Result<List<StoryEntity>>> = flow {
        emit(Result.Loading)
        try {
            val token = userPreferences.getToken().first()
            val response = apiService.getAllStories("Bearer $token", location = 1)
            val result = DataMapper.mapStoryResponseToStoryEntity(response.listStory)

            if (response.error) {
                emit(Result.Error(response.message))
            } else {
                emit(Result.Success(result))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)

    fun addNewStory(
        photo: File,
        description: String,
        location: Location?,
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

        var latRequestBody: RequestBody? = null
        var lonRequestBody: RequestBody? = null
        location?.let {
            latRequestBody = it.latitude.toString().toRequestBody(textPlainMediaType)
            lonRequestBody = it.longitude.toString().toRequestBody(textPlainMediaType)
        }

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

            if (response.error) {
                emit(Result.Error(response.message))
            } else {
                emit(Result.Success(response))
            }

        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)

    fun checkCredential(): Flow<Boolean> = userPreferences.checkCredential()
    suspend fun saveCredential(token: String) =
        userPreferences.saveCredential(token)

    suspend fun deleteCredential() = userPreferences.deleteCredential()

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