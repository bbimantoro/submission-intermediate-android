package com.academy.bangkit.mystoryapp.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.academy.bangkit.mystoryapp.data.UserPreferences
import com.academy.bangkit.mystoryapp.data.Result
import com.academy.bangkit.mystoryapp.data.network.retrofit.ApiConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(
    private val pref: UserPreferences
) : ViewModel() {

    private var _result = MutableLiveData<Result<List<Story>>>()
    val result: LiveData<Result<List<Story>>> = _result

    fun getAllStories(token: String) {
        viewModelScope.launch {
            _result.value = Result.Loading
            try {
                val response = ApiConfig.getApiService().getAllStories(token)
                _result.value = Result.Success(response.listStory)
            } catch (e: Exception) {
                Log.d("MainViewModel", "getAllStories: ${e.message.toString()}")
                _result.value = Result.Error(e.message.toString())
            }
        }
    }

    fun getToken(): LiveData<String> {
        return pref.getToken().asLiveData()
    }

    fun destroyToken() {
        viewModelScope.launch(Dispatchers.IO) {
            pref.destroyToken()
        }
    }
}