package com.innovu.visitor.ui.chat

import android.R
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.innovu.visitor.data.api.RetrofitClient
import com.innovu.visitor.model.ChatMessage
import com.innovu.visitor.model.ChatPostRequest
import com.innovu.visitor.model.ChatUser
import com.innovu.visitor.model.ChatUserModel
import com.innovu.visitor.utlis.StorePrefData
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class ChatViewModel : ViewModel() {

    private val _staffList = MutableLiveData<List<ChatUserModel>>()
    val staffList: LiveData<List<ChatUserModel>> = _staffList

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error



    private val _chatList = MutableLiveData<List<ChatMessage>>()
    val chatList: LiveData<List<ChatMessage>> = _chatList


    private val _chatuser = MutableLiveData<List<ChatUser>>()
    val chatuser: LiveData<List<ChatUser>> = _chatuser



    private val _ChatStatus = MutableLiveData<Boolean>()
    val ChatStatus: LiveData<Boolean> = _ChatStatus

    fun fetChatUser() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.GetChatUserByOrg(StorePrefData.UserIId, StorePrefData.OrgId)
                if (response.isSuccessful && response.body()?.success == true) {
                    _staffList.postValue(response.body()?.data ?: emptyList())
                } else {
                    val errorMsg = response?.message() ?: "Unknown API error"
                    Log.e("API", "API Error: $errorMsg")
                    _error.postValue(errorMsg)
                }
            } catch (e: Exception) {
                _error.postValue("Exception: ${e.localizedMessage}")
            }
        }
    }

    fun fetChatMessage( recId:Int) {
        viewModelScope.launch {
            try {

                val response = RetrofitClient.instance.getChatMessages(StorePrefData.UserIId, recId)
                if (response.isSuccessful ) {
                    val body = response.body()
                    if (body?.success == true && body.data?.isJsonArray == true) {
                        // Success with proper data
                        val chatList = Gson().fromJson(
                            body.data,
                            Array<ChatMessage>::class.java
                        ).toList()
                        _chatList.postValue(chatList ?: emptyList())
                    } else {
                        // API returned success = false with message like "No chat found"
                        val errorMsg = response.body()?.message ?: "Unknown API error"
                        Log.e("API", "API Error: $errorMsg")
                        _error.postValue(errorMsg)
                    }

                } else {
                    _error.postValue("API error: ${response.message()}")
                }
            } catch (e: Exception) {
                _error.postValue("Exception: ${e.localizedMessage}")
            }
        }
    }



    fun postChatMessage( recId:Int,message: String) {

        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
        val nowUtc = currentDateTime.format(formatter)


        val chatRequest = ChatPostRequest(
            chatID = 0,
            organizationID = StorePrefData.OrgId,
            senderID = StorePrefData.UserIId,
            receiverID = recId,
            isActive = true,
            message = message,
            attachmentPath = "",
            sentAt = nowUtc,
            readAt = nowUtc,
            createdAt = nowUtc,
            updatedAt = nowUtc
        )

        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.postChatMessage(chatRequest)
                if (response.isSuccessful ) {






                    _ChatStatus.postValue(response.isSuccessful)
                } else {
                    _error.postValue("API error: ${response.message()}")
                }
            } catch (e: Exception) {
                _error.postValue("Exception: ${e.localizedMessage}")
            }
        }
    }


    fun getChatUserList() {
        viewModelScope.launch {
            try {

                val response = RetrofitClient.instance.getChatUserList(StorePrefData.UserIId)
                if (response.isSuccessful ) {


                    if (response?.body()!!.success == true) {
                        // Success with proper data
                        _chatuser.postValue(response.body()?.data ?: emptyList())
                    } else {
                        // API returned success = false with message like "No chat found"
                        val errorMsg = response.body()?.message ?: "Unknown API error"
                        Log.e("API", "API Error: $errorMsg")
                        _error.postValue(errorMsg)
                    }


                } else {
                    _error.postValue("API error: ${response.message()}")
                }
            } catch (e: Exception) {
                _error.postValue("Exception: ${e.localizedMessage}")
            }
        }
    }




}