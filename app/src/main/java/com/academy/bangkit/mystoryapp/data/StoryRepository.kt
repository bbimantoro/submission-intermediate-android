package com.academy.bangkit.mystoryapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.academy.bangkit.mystoryapp.data.network.response.CommonResponse
import com.academy.bangkit.mystoryapp.data.network.response.Story
import com.academy.bangkit.mystoryapp.data.network.retrofit.ApiConfig
import com.academy.bangkit.mystoryapp.data.network.retrofit.StoryApiService
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class StoryRepository private constructor(
    private val apiService: StoryApiService,
    private val userPreferences: UserPreferences
) {

    fun getAllStories(token: String): LiveData<Result<List<Story>>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getAllStories(token)

            if (response.error) {
                emit(Result.Error(response.message))
            } else {
                emit(Result.Success(response.listStory))
            }

        } catch (e: Exception) {
            Log.d("MainViewModel", "getAllStories: ${e.message.toString()}")
            Result.Error(e.message.toString())
        }
    }

    fun addNewStory(
        token: String,
        photo: File,
        description: String
    ): LiveData<Result<CommonResponse>> = liveData {
        emit(Result.Loading)

        val textPlainMediaType = "text/plain".toMediaType()
        val imageMediaType = "image/jpeg".toMediaType()

        val imageMultiPart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            photo.name,
            photo.asRequestBody(imageMediaType)
        )

        val descriptionRequestBody = description.toRequestBody(textPlainMediaType)

        try {
            val response = apiService.addNewStory(token, imageMultiPart, descriptionRequestBody)

            if (response.error) {
                emit(Result.Error(response.message))
            } else {
                emit(Result.Success(response))
            }

        } catch (e: Exception) {
            Log.d("PostStoryViewModel", "addNewStory: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getToken(): LiveData<String> = userPreferences.getToken().asLiveData()

    suspend fun login(token: String, session: Boolean) {
        userPreferences.login(token, session)
    }

    suspend fun logout() {
        userPreferences.logout()
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(
            apiService: StoryApiService,
            userPreferences: UserPreferences
        ): StoryRepository = instance ?: synchronized(this) {
            instance ?: StoryRepository(apiService, userPreferences)
        }.also { instance = it }
    }

}