package com.academy.bangkit.mystoryapp.ui.story.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.academy.bangkit.mystoryapp.data.local.entity.StoryEntity
import com.academy.bangkit.mystoryapp.data.repository.StoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainViewModel(
    private val storyRepository: StoryRepository
) : ViewModel() {

    private val _storyResult = MutableLiveData<PagingData<StoryEntity>>()
    val storyResult: LiveData<PagingData<StoryEntity>> = _storyResult

    init {
        getAllStories()
    }

    private fun getAllStories() {
        viewModelScope.launch {
            storyRepository.getStories().cachedIn(viewModelScope).collect {
                _storyResult.value = it
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            storyRepository.deleteCredential()
        }
    }

}