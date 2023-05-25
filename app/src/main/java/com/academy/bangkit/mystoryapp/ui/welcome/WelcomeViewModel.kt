package com.academy.bangkit.mystoryapp.ui.welcome

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.academy.bangkit.mystoryapp.data.UserPreferences

class WelcomeViewModel(private val pref: UserPreferences) : ViewModel() {

    fun checkIsLogin(): LiveData<Boolean> = pref.getLogin().asLiveData()
}