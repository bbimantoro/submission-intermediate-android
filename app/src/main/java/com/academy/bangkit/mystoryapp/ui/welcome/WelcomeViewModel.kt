package com.academy.bangkit.mystoryapp.ui.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.academy.bangkit.mystoryapp.data.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WelcomeViewModel(private val pref: UserPreferences) : ViewModel() {

    fun setIsLogin(isLogin: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            pref.setIsLogin(isLogin)
        }
    }
}