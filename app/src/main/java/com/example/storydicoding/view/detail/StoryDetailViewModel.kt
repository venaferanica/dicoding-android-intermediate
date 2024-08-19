package com.example.storydicoding.view.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.storydicoding.UserPreference
import com.example.storydicoding.api.ApiConfig
import com.example.storydicoding.api.response.DetailStoriesResponse
import com.example.storydicoding.api.response.LoginResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryDetailViewModel (private val pref: UserPreference) : ViewModel() {

    private val _detailName = MutableLiveData<String>()
    val detailName: LiveData<String> = _detailName

    private val _detailDesc = MutableLiveData<String>()
    val detailDesc: LiveData<String> = _detailDesc

    private val _detailPhotoUrl = MutableLiveData<String>()
    val detailPhotoUrl: LiveData<String> = _detailPhotoUrl

    private val _detailId = MutableLiveData<String>()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getDetail(token: String, id: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getDetailStory(token, id)
        client.enqueue(object: Callback<DetailStoriesResponse> {
            override fun onResponse(
                call: Call<DetailStoriesResponse>,
                response: Response<DetailStoriesResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    _isLoading.value = false

                    _detailId.value = responseBody?.story?.id.toString()
                    _detailName.value = responseBody?.story?.name.toString()
                    _detailDesc.value = responseBody?.story?.description.toString()
                    _detailPhotoUrl.value = responseBody?.story?.photoUrl.toString()
                } else {
                    _isLoading.value = false
                }
            }
            override fun onFailure(call: Call<DetailStoriesResponse>, t: Throwable) {
                _isLoading.value = false
            }
        })
    }

    fun getUser(): LiveData<LoginResult> {
        return pref.getUser().asLiveData()
    }

    companion object{
        private const val TAG = "DetailStoryViewModel"
    }
}