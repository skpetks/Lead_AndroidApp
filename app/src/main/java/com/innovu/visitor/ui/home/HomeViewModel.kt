package com.innovu.visitor.ui.home

import android.R
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innovu.visitor.data.api.ApiService
import com.innovu.visitor.data.api.RetrofitClient
import com.innovu.visitor.model.DeviceTokenRequest
import com.innovu.visitor.model.GateDetail
import com.innovu.visitor.utlis.StorePrefData
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text


    private val _gates = MutableLiveData<List<GateDetail>>()
    val gates: LiveData<List<GateDetail>> = _gates

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error



    // Function to call API
    fun fetchGateDetails(apiService: ApiService) {
        viewModelScope.launch {
            try {
                val response = apiService.GetAllGateDetailsByOrg(StorePrefData.OrgId,StorePrefData.BranchId)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.success) {
                        _gates.value = body.data
                    } else {
                        _error.value = "No data or unsuccessful response"
                    }
                } else {
                    _error.value = "Error code: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Exception: ${e.localizedMessage}"
            }
        }
    }


    fun updateToken(versionName: String) {
        viewModelScope.launch {
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                sdf.timeZone = TimeZone.getTimeZone("UTC")
                val currentDateTime = sdf.format(Date())
                val request = DeviceTokenRequest(
                    userId = StorePrefData.UserIId,
                    deviceToken = StorePrefData.token,
                    deviceType = "android",
                    appVersion = versionName,
                    createdAt = currentDateTime,
                    updatedAt = currentDateTime,
                    deviceUID=StorePrefData.UID,
                )


                val response = RetrofitClient.instance.updateToken(request)
                if (response.isSuccessful ) {
                    Log.d("updateToken", "Token updated:")
                } else {
                    _error.postValue("API error: ${response.message()}")

                    Log.d("updateToken", "updateToken message: ${response.message()}")
                }
            } catch (e: Exception) {
                _error.postValue("Exception: ${e.localizedMessage}")
            }
        }
    }
}