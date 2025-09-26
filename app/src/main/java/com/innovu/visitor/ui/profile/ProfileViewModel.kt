package com.innovu.visitor.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innovu.visitor.data.api.RetrofitClient
import com.innovu.visitor.model.UserProfile
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Create Visitor Fragment"
    }
    val text: LiveData<String> = _text

    private val _userProfile = MutableLiveData<UserProfile?>()
    val userProfile: LiveData<UserProfile?> = _userProfile

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun fetchUserProfile(userId: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.getUserProfile(userId)
                if (response.isSuccessful && response.body()?.success == true) {
                    _userProfile.postValue(response.body()?.data)
                } else {
                    _error.postValue("Failed: ${response.body()?.message ?: response.message()}")
                }
            } catch (e: Exception) {
                _error.postValue("Exception: ${e.localizedMessage}")
            }
        }
    }
}