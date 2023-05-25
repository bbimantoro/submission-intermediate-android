package com.academy.bangkit.mystoryapp.data.network.retrofit

import com.academy.bangkit.mystoryapp.data.network.response.CommonResponse
import com.academy.bangkit.mystoryapp.data.network.response.LoginResponse
import com.academy.bangkit.mystoryapp.data.network.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface StoryApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun signup(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): CommonResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("stories")
    suspend fun getAllStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
    ): StoryResponse

    @Multipart
    @POST("stories")
    suspend fun addNewStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): CommonResponse

}