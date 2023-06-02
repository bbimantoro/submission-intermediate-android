package com.academy.bangkit.mystoryapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.academy.bangkit.mystoryapp.data.local.datastore.UserPreferences
import com.academy.bangkit.mystoryapp.data.local.room.StoryDatabase
import com.academy.bangkit.mystoryapp.data.network.retrofit.ApiConfig
import com.academy.bangkit.mystoryapp.data.network.retrofit.StoryApiService
import com.academy.bangkit.mystoryapp.data.repository.StoryRepository


private const val USER_PREFERENCES = "user_preferences"

object Injection {

    private fun provideDatabase(context: Context): StoryDatabase =
        StoryDatabase.getInstance(context)

    private fun provideApiService(): StoryApiService = ApiConfig.getApiService()

    private fun provideUserPreferences(context: Context): UserPreferences =
        UserPreferences.getInstance(providePreferencesDataStore(context))

    private fun providePreferencesDataStore(context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(produceFile = {
            context.preferencesDataStoreFile(
                USER_PREFERENCES
            )
        })
    }

    fun provideStoryRepository(context: Context): StoryRepository {
        return StoryRepository.getInstance(
            provideDatabase(context),
            provideApiService(),
            provideUserPreferences(context)
        )
    }

}