package com.academy.bangkit.mystoryapp.data.network.response

import com.google.gson.annotations.SerializedName

data class StoryResponse(

    @field:SerializedName("listStory")
    val listStory: List<Story>,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)

data class Story(

    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("photoUrl")
    val photoUrl: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("createdAt")
    val createdAt: String? = null,

    @field:SerializedName("lon")
    val lon: Double,

    @field:SerializedName("lat")
    val lat: Double
)