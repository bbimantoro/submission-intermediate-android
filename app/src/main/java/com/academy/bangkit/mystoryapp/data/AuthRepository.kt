package com.academy.bangkit.mystoryapp.data

import com.academy.bangkit.mystoryapp.data.network.retrofit.StoryApiService

class AuthRepository private constructor(
    private val storyApiService: StoryApiService
) {

}