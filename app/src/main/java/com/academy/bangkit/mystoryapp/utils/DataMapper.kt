package com.academy.bangkit.mystoryapp.utils

import com.academy.bangkit.mystoryapp.data.local.entity.StoryEntity
import com.academy.bangkit.mystoryapp.data.network.response.Story

object DataMapper {

    fun mapStoryResponseToStoryEntity(input: List<Story>): List<StoryEntity> =
        input.map {
            StoryEntity(
                id = it.id,
                createdAt = it.createdAt,
                photoUrl = it.photoUrl,
                name = it.name,
                description = it.description,
                lon = it.lon,
                lat = it.lat
            )
        }

    fun mapStoryEntityToStoryResponse(input: List<StoryEntity>): List<Story> =
        input.map {
            Story(
                id = it.id,
                createdAt = it.createdAt,
                photoUrl = it.photoUrl,
                name = it.name,
                description = it.description,
                lon = it.lon,
                lat = it.lat
            )
        }
}