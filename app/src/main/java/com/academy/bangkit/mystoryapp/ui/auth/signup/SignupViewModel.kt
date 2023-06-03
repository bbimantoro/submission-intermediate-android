package com.academy.bangkit.mystoryapp.ui.auth.signup

import android.util.Log
import com.academy.bangkit.mystoryapp.data.Result
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.academy.bangkit.mystoryapp.data.network.response.CommonResponse
import com.academy.bangkit.mystoryapp.data.network.retrofit.ApiConfig
import com.academy.bangkit.mystoryapp.data.repository.StoryRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SignupViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    private val _signupResult = MutableLiveData<Result<CommonResponse>>()
    val signupResult: LiveData<Result<CommonResponse>> = _signupResult

    fun signup(name: String, email: String, password: String) {
        viewModelScope.launch {
            storyRepository.signup(name, email, password).collect {
                _signupResult.value = it
            }
        }
    }
}