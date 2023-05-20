package com.academy.bangkit.mystoryapp.ui.auth.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.academy.bangkit.mystoryapp.data.Result
import com.academy.bangkit.mystoryapp.data.network.response.LoginResponse
import com.academy.bangkit.mystoryapp.data.network.retrofit.ApiConfig
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private var _result = MutableLiveData<Result<LoginResponse>>()
    val result: LiveData<Result<LoginResponse>> get() = _result

    fun login(email: String, password: String) {
        _result.value = Result.Loading
        viewModelScope.launch {
            try {
                val response = ApiConfig.getApiService().login(email, password)
                _result.value = Result.Success(response)
            } catch (e: Exception) {
                Log.d(TAG, "login: ${e.message.toString()}")
                _result.value = Result.Error(e.message.toString())
            }
        }
    }

    companion object {
        const val TAG = "LoginViewModel"
    }
}