package com.academy.bangkit.mystoryapp.utils

import com.academy.bangkit.mystoryapp.data.local.entity.StoryEntity

object DataDummy {
    fun generateDummyStoryEntity(): List<StoryEntity> {
        val storyList = ArrayList<StoryEntity>()
        for (i in 0..10) {
            val story = StoryEntity(
                i.toString(),
                "photo $i",
                "name $i",
                "description $i",
                "2022-01-08T06:34:18.598Z",
                -16.002,
                -10.212,
            )
            storyList.add(story)
        }
        return storyList
    }
}