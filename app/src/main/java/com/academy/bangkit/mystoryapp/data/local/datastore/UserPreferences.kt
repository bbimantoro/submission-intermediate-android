package com.academy.bangkit.mystoryapp.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    fun getToken(): Flow<String> {
        return dataStore.data.map {
            it[TOKEN_KEY] ?: ""
        }
    }

    suspend fun saveCredential(token: String) {
        dataStore.edit {
            it[TOKEN_KEY] = token
            it[SESSION_KEY] = true
        }
    }

    suspend fun deleteCredential() {
        dataStore.edit {
            it.remove(TOKEN_KEY)
            it[SESSION_KEY] = false
        }
    }

    fun checkCredential(): Flow<Boolean> {
        return dataStore.data.map {
            it[SESSION_KEY] ?: false
        }
    }

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val SESSION_KEY = booleanPreferencesKey("session")

        @Volatile
        private var instance: UserPreferences? = null
        fun getInstance(dataStore: DataStore<Preferences>): UserPreferences =
            instance ?: synchronized(this) {
                instance ?: UserPreferences(dataStore)
            }.also { instance = it }
    }
}