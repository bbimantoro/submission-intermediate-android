package com.academy.bangkit.mystoryapp.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.academy.bangkit.mystoryapp.data.repository.StoryRepository
import com.academy.bangkit.mystoryapp.di.Injection
import com.academy.bangkit.mystoryapp.ui.auth.login.LoginViewModel
import com.academy.bangkit.mystoryapp.ui.auth.signup.SignupViewModel
import com.academy.bangkit.mystoryapp.ui.story.main.MainViewModel
import com.academy.bangkit.mystoryapp.ui.story.maps.MapsViewModel
import com.academy.bangkit.mystoryapp.ui.story.post.PostStoryViewModel
import com.academy.bangkit.mystoryapp.ui.welcome.WelcomeViewModel

class ViewModelFactory(private val storyRepository: StoryRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(storyRepository) as T
            }

            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(storyRepository) as T
            }

            modelClass.isAssignableFrom(PostStoryViewModel::class.java) -> {
                PostStoryViewModel(storyRepository) as T
            }

            modelClass.isAssignableFrom(WelcomeViewModel::class.java) -> {
                WelcomeViewModel(storyRepository) as T
            }

            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(storyRepository) as T
            }

            modelClass.isAssignableFrom(SignupViewModel::class.java) -> {
                SignupViewModel(storyRepository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory = instance ?: synchronized(this) {
            instance ?: ViewModelFactory(Injection.provideStoryRepository(context))
        }.also { instance = it }
    }
}