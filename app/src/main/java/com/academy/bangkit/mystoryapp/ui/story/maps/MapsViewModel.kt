package com.academy.bangkit.mystoryapp.ui.story.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.academy.bangkit.mystoryapp.data.Result
import com.academy.bangkit.mystoryapp.data.local.entity.StoryEntity
import com.academy.bangkit.mystoryapp.data.repository.StoryRepository
import kotlinx.coroutines.launch

class MapsViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val _storyMapsResult = MutableLiveData<Result<List<StoryEntity>>>()
    val storyMapsResult: LiveData<Result<List<StoryEntity>>> = _storyMapsResult

    fun getStoriesWithLocation() {
        viewModelScope.launch {
            storyRepository.getStoriesWithLocation().collect {
                _storyMapsResult.value = it
            }
        }
    }
}