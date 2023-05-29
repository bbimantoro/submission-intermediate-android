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
import kotlinx.coroutines.launch

class LoginViewModel(private val pref: UserPreferences) : ViewModel() {

    private val _result = MutableLiveData<Result<LoginResponse>>()
    val result: LiveData<Result<LoginResponse>> = _result

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _result.value = Result.Loading
            try {
                val response = ApiConfig.getApiService().login(email, password)

                if (response.error) {
                    _result.value = Result.Error(response.message)
                } else {
                    _result.value = Result.Success(response)
                }

            } catch (e: Exception) {
                _result.value = Result.Error(e.message.toString())
            }
        }
    }

    fun setLogin(session: Boolean) {
        viewModelScope.launch {
            pref.setLogin(session)
        }
    }

    fun saveToken(token: String) {
        viewModelScope.launch {
            pref.saveToken(token)
        }
    }
}