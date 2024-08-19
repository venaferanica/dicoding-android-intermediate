package com.example.storydicoding.view.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.storydicoding.UserPreference
import com.example.storydicoding.api.ApiConfig
import com.example.storydicoding.api.response.ListStoryItem
import com.example.storydicoding.api.response.LoginResult
import com.example.storydicoding.api.response.StoryResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModel (private val pref: UserPreference) : ViewModel() {

    private val _stories = MutableLiveData<List<ListStoryItem>>()
    val stories: LiveData<List<ListStoryItem>> = _stories

    private val _isLoading = MutableLiveData<Boolean>()

    fun getStories(token: String, location: Int = 1) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getStoriesWithLocation(token, location)
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    _stories.value = responseBody?.listStory as List<ListStoryItem>
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                _isLoading.value = false
            }
        })
    }

    fun getUser(): LiveData<LoginResult> {
        return pref.getUser().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }

    companion object{
        private const val TAG = "MapsViewModel"
    }
}