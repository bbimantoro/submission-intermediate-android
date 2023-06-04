package com.academy.bangkit.mystoryapp.ui.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.academy.bangkit.mystoryapp.data.Result
import com.academy.bangkit.mystoryapp.data.local.datastore.UserPreferences
import com.academy.bangkit.mystoryapp.data.network.response.LoginResponse
import com.academy.bangkit.mystoryapp.data.network.retrofit.ApiConfig
import com.academy.bangkit.mystoryapp.data.repository.StoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LoginViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> = _loginResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            storyRepository.login(email, password).collect {
                _loginResult.value = it
            }
        }
    }
    fun saveCredential(token: String) {
        viewModelScope.launch {
            storyRepository.saveCredential(token)
        }
    }
}