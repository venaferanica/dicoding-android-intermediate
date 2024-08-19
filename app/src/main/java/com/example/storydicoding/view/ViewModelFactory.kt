package com.example.storydicoding.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storydicoding.Injection
import com.example.storydicoding.StoryRepository
import com.example.storydicoding.UserPreference
import com.example.storydicoding.view.detail.StoryDetailViewModel
import com.example.storydicoding.view.login.LoginViewModel
import com.example.storydicoding.view.main.MainViewModel
import com.example.storydicoding.view.maps.MapsViewModel
import com.example.storydicoding.view.upload.UploadViewModel

class ViewModelFactory(
    private val storyRepository: StoryRepository,
    private val userPreference: UserPreference
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> MainViewModel(storyRepository, userPreference) as T
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> LoginViewModel(userPreference) as T
            modelClass.isAssignableFrom(StoryDetailViewModel::class.java) -> StoryDetailViewModel(userPreference) as T
            modelClass.isAssignableFrom(UploadViewModel::class.java) -> UploadViewModel(userPreference) as T
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> MapsViewModel(userPreference) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory {
            return INSTANCE ?: synchronized(this) {
                val storyRepository = Injection.provideRepository(context)
                val userPreference = Injection.providePreference(context)
                ViewModelFactory(storyRepository, userPreference).also { INSTANCE = it }
            }
        }
    }
}