package com.example.matricareog

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreManager(private val context: Context) {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("auth_preferences")

        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val USER_ID = stringPreferencesKey("user_id")
        private val USER_EMAIL = stringPreferencesKey("user_email")
        private val USER_NAME = stringPreferencesKey("user_name")
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_LOGGED_IN] ?: false
    }

    val userId: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_ID]
    }

    val userEmail: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_EMAIL]
    }

    val userName: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_NAME]
    }

    suspend fun saveUserSession(userId: String, email: String, name: String) {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = true
            preferences[USER_ID] = userId
            preferences[USER_EMAIL] = email
            preferences[USER_NAME] = name
        }
    }

    suspend fun clearUserSession() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}