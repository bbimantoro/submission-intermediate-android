package com.academy.bangkit.mystoryapp.ui.auth.signup

import android.util.Log
import com.academy.bangkit.mystoryapp.data.Result
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.academy.bangkit.mystoryapp.data.network.response.CommonResponse
import com.academy.bangkit.mystoryapp.data.network.retrofit.ApiConfig
import kotlinx.coroutines.launch

class SignupViewModel : ViewModel() {
    private val _result = MutableLiveData<Result<CommonResponse>>()
    val result: LiveData<Result<CommonResponse>> = _result

    fun signup(name: String, email: String, password: String) {
        viewModelScope.launch {
            _result.value = Result.Loading
            try {
                val response = ApiConfig.getApiService().signup(name, email, password)
                _result.value = Result.Success(response)
            } catch (e: Exception) {
                Log.d(TAG, "signup: ${e.message.toString()}")
                _result.value = Result.Error(e.message.toString())
            }
        }
    }

    companion object {
        private const val TAG = "SignupViewModel"
    }
}