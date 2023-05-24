package com.academy.bangkit.mystoryapp.data

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

    suspend fun login(token: String, session: Boolean) {
        dataStore.edit {
            it[TOKEN_KEY] = token
            it[SESSION_KEY] = session
        }
    }

    suspend fun logout() {
        dataStore.edit {
            it[TOKEN_KEY] = ""
            it[SESSION_KEY] = false
        }
    }

    suspend fun saveToken(token: String) {
        dataStore.edit {
            it[TOKEN_KEY] = token
        }
    }

    suspend fun destroyToken() {
        dataStore.edit {
            it[TOKEN_KEY] = ""
        }
    }

    suspend fun setIsLogin(session: Boolean) {
        dataStore.edit {
            it[SESSION_KEY] = session
        }
    }

    fun isLogin(): Flow<Boolean> {
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