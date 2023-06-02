package com.academy.bangkit.mystoryapp.ui.welcome

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.academy.bangkit.mystoryapp.data.local.datastore.UserPreferences
import com.academy.bangkit.mystoryapp.data.repository.StoryRepository

class WelcomeViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun isLogin(): LiveData<Boolean> = storyRepository.getLogin().asLiveData()
}