package com.example.storydicoding.view.upload

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.storydicoding.Event
import com.example.storydicoding.UserPreference
import com.example.storydicoding.api.ApiConfig
import com.example.storydicoding.api.response.AddNewStoryResponse
import com.example.storydicoding.api.response.LoginResult
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UploadViewModel (private val pref: UserPreference) : ViewModel() {

    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess: LiveData<Boolean> = _isSuccess

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _statusMessage = MutableLiveData<Event<String>>()
    val statusMessage : LiveData<Event<String>> = _statusMessage

    fun getUser(): LiveData<LoginResult> {
        return pref.getUser().asLiveData()
    }

    fun uploadStory(token: String, image: MultipartBody.Part, description: RequestBody){
        _isLoading.value = true
        val client = ApiConfig.getApiService().uploadStory(token, image, description)
        client.enqueue(object : Callback<AddNewStoryResponse> {
            override fun onResponse(
                call: Call<AddNewStoryResponse>,
                response: Response<AddNewStoryResponse>
            ) {
                if (response.isSuccessful) {
                    _isLoading.value = false
                    _isSuccess.value = true
                    _statusMessage.value = Event("Upload successful")

                } else {
                    _isLoading.value = false
                    _isSuccess.value = false
                    _statusMessage.value = Event("Failed to upload")
                }
            }

            override fun onFailure(call: Call<AddNewStoryResponse>, t: Throwable) {
                _isLoading.value = false
                _isSuccess.value = false
            }
        })
    }

    companion object{
        private const val TAG = "UploadStoryViewModel"
    }
}