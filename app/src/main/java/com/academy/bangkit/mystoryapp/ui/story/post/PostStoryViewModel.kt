package com.academy.bangkit.mystoryapp.ui.story.post

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.academy.bangkit.mystoryapp.data.Result
import com.academy.bangkit.mystoryapp.data.network.response.CommonResponse
import com.academy.bangkit.mystoryapp.data.network.retrofit.ApiConfig
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class PostStoryViewModel : ViewModel() {

    private var _result = MutableLiveData<Result<CommonResponse>>()
    val result: LiveData<Result<CommonResponse>> = _result

    fun addNewStory(token: String, photo: File, description: String) {
        viewModelScope.launch {
            _result.value = Result.Loading

            val textPlainMediaType = "text/plain".toMediaType()
            val imageMediaType = "image/jpeg".toMediaType()

            val imageMultiPart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                photo.name,
                photo.asRequestBody(imageMediaType)
            )

            val descriptionRequestBody = description.toRequestBody(textPlainMediaType)

            try {
                val response = ApiConfig.getApiService()
                    .addNewStory(token, imageMultiPart, descriptionRequestBody)

                if (response.error) {
                    _result.value = Result.Error(response.message)
                } else {
                    _result.value = Result.Success(response)
                }

            } catch (e: Exception) {
                Log.d("PostStoryViewModel", "addNewStory: ${e.message.toString()}")
                _result.value = Result.Error(e.message.toString())
            }
        }
    }
}