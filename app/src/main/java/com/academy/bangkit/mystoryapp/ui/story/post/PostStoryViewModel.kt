package com.academy.bangkit.mystoryapp.ui.story.post

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.academy.bangkit.mystoryapp.data.Result
import com.academy.bangkit.mystoryapp.data.network.response.CommonResponse
import com.academy.bangkit.mystoryapp.data.repository.StoryRepository
import kotlinx.coroutines.launch
import java.io.File

class PostStoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val _result = MutableLiveData<Result<CommonResponse>>()
    val result: LiveData<Result<CommonResponse>> = _result

    fun addNewStory(
        photo: File,
        description: String,
        location: Location? = null,
    ) {
        viewModelScope.launch {
            storyRepository.addNewStory(photo, description, location).collect {
                _result.value = it
            }
        }
    }
}