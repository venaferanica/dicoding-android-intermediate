package com.example.storydicoding

import android.content.Context
import com.example.storydicoding.api.ApiConfig
import com.example.storydicoding.view.main.dataStore

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val apiService = ApiConfig.getApiService()
        val pref = UserPreference.getInstance(context.dataStore)
        return StoryRepository(apiService, pref)
    }

    fun providePreference(context: Context): UserPreference {
        return UserPreference.getInstance(context.dataStore)
    }
}