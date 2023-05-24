package com.academy.bangkit.mystoryapp.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.academy.bangkit.mystoryapp.data.StoryRepository
import com.academy.bangkit.mystoryapp.data.UserPreferences
import com.academy.bangkit.mystoryapp.data.network.retrofit.ApiConfig

private const val USER_PREFERENCES = "user_preferences"

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        return StoryRepository.getInstance(provideApiService(), provideUserPreferences(context))
    }

    private fun providePreferencesDataStore(context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = {
                context.preferencesDataStoreFile(
                    USER_PREFERENCES
                )
            }
        )
    }

    private fun provideApiService() = ApiConfig.getApiService()

    private fun provideUserPreferences(context: Context) =
        UserPreferences.getInstance(providePreferencesDataStore(context))
}