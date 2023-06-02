package com.academy.bangkit.mystoryapp.ui.story.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.academy.bangkit.mystoryapp.data.Result
import com.academy.bangkit.mystoryapp.data.local.datastore.UserPreferences
import com.academy.bangkit.mystoryapp.data.network.response.Story
import com.academy.bangkit.mystoryapp.data.network.retrofit.ApiConfig
import com.academy.bangkit.mystoryapp.data.repository.StoryRepository
import kotlinx.coroutines.launch

class MainViewModel(
    private val storyRepository: StoryRepository
) : ViewModel() {

    private val _result = MutableLiveData<Result<List<Story>>>()
    val result: LiveData<Result<List<Story>>> = _result

    fun getAllStories(token: String) {
        viewModelScope.launch {
            storyRepository.getStories()
        }
    }

    fun logout() {
        viewModelScope.launch {
            storyRepository.deleteCredential()
        }
    }
}