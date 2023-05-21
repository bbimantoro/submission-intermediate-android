package com.academy.bangkit.mystoryapp.ui.auth.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.academy.bangkit.mystoryapp.data.Result
import com.academy.bangkit.mystoryapp.data.UserPreferences
import com.academy.bangkit.mystoryapp.data.network.response.LoginResponse
import com.academy.bangkit.mystoryapp.data.network.retrofit.ApiConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(private val pref: UserPreferences) : ViewModel() {

    private var _result = MutableLiveData<Result<LoginResponse>>()
    val result: LiveData<Result<LoginResponse>> = _result

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _result.value = Result.Loading
            try {
                val response = ApiConfig.getApiService().login(email, password)
                _result.value = Result.Success(response)
            } catch (e: Exception) {
                Log.d(TAG, "login: ${e.message.toString()}")
                _result.value = Result.Error(e.message.toString())
            }
        }
    }

    fun saveToken(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            pref.saveToken(token)
        }
    }

    companion object {
        const val TAG = "LoginViewModel"
    }
}