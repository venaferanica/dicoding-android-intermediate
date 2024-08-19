package com.example.storydicoding.view.login

import android.util.Log
import androidx.lifecycle.*
import com.example.storydicoding.api.response.LoginResponse
import com.example.storydicoding.Event
import com.example.storydicoding.UserPreference
import com.example.storydicoding.api.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel (private val pref: UserPreference) : ViewModel() {
    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> = _userName

    private val _userId = MutableLiveData<String>()
    val userId: LiveData<String> = _userId

    private val _userToken = MutableLiveData<String>()
    val userToken: LiveData<String> = _userToken

    private val _isLoading = MutableLiveData<Boolean>()

    private val _statusMessage = MutableLiveData<Event<String>>()
    val statusMessage : LiveData<Event<String>> = _statusMessage

    fun login(email: String, password: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().login(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                        _isLoading.value = false
                        _userName.value = responseBody?.loginResult?.name.toString()
                        _userId.value = responseBody?.loginResult?.userId.toString()
                        _userToken.value = responseBody?.loginResult?.token.toString()

                        viewModelScope.launch {
                            pref.saveUser(userName.value.toString(), userId.value.toString(), "Bearer " + userToken.value.toString())
                        }

                    _statusMessage.value = Event("Login successful")
                    Log.d(TAG, userToken.value.toString())
                } else {
                    _isLoading.value = false
                    _statusMessage.value = Event("Login failed! Please ensure the email and password are valid.")
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
            }
        })
    }

    companion object{
        private const val TAG = "LoginViewModel"
    }
}