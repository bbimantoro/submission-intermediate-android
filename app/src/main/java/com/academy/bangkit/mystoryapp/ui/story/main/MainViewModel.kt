package com.academy.bangkit.mystoryapp.ui.story.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.academy.bangkit.mystoryapp.data.Result
import com.academy.bangkit.mystoryapp.data.UserPreferences
import com.academy.bangkit.mystoryapp.data.network.response.Story
import com.academy.bangkit.mystoryapp.data.network.retrofit.ApiConfig
import kotlinx.coroutines.launch

class MainViewModel(
    private val pref: UserPreferences
) : ViewModel() {

    private val _result = MutableLiveData<Result<List<Story>>>()
    val result: LiveData<Result<List<Story>>> = _result

    fun getAllStories(token: String) {
        viewModelScope.launch {
            _result.value = Result.Loading
            try {
                val response = ApiConfig.getApiService().getAllStories(token)

                if (response.error) {
                    _result.value = Result.Error(response.message)
                } else {
                    _result.value = Result.Success(response.listStory)
                }
            } catch (e: Exception) {
                _result.value = Result.Error(e.message.toString())
            }
        }
    }

    fun getToken(): LiveData<String> {
        return pref.getToken().asLiveData()
    }

    fun setLogin(session: Boolean) {
        viewModelScope.launch {
            pref.setLogin(session)
        }
    }


    fun logout() {
        viewModelScope.launch {
            pref.destroyToken()
        }
    }
}