package com.academy.bangkit.mystoryapp.ui.story.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.academy.bangkit.mystoryapp.data.Result
import com.academy.bangkit.mystoryapp.data.StoryRepository
import com.academy.bangkit.mystoryapp.data.network.response.Story
import kotlinx.coroutines.launch

class MainViewModelTest(private val storyRepository: StoryRepository) : ViewModel() {

    private var _result = MutableLiveData<Result<List<Story>>>()
    val result: LiveData<Result<List<Story>>> = _result

    private var _tokenResult = MutableLiveData<String>()
    val tokenResult: LiveData<String> = _tokenResult

    fun getAllStories(token: String) {
        viewModelScope.launch {
            storyRepository.getAllStories(token = "Bearer $token")
        }
    }

    private fun getToken() {
        viewModelScope.launch {
            storyRepository.getToken()
        }
    }
}