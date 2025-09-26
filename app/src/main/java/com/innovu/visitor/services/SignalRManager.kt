package com.innovu.visitor.services

// File: SignalRManager.kt

import android.media.RingtoneManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.innovu.visitor.BuildConfig
import com.innovu.visitor.model.ChatMessage
import com.innovu.visitor.utlis.StorePrefData
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import com.microsoft.signalr.TypeReference
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable

object SignalRManager {

    private var isConnected = false
    val uid = StorePrefData.UserID
    val hubUrl = BuildConfig.SERVICE_END_POINT.replace("/api/", "/") + "chathub?userId=$uid"



    private const val TAG = "SignalRManager"

    lateinit var hubConnection: HubConnection
    private var connectionDisposable: Disposable? = null
    var onConnected: (() -> Unit)? = null
    var onDisconnected: (() -> Unit)? = null
    fun initConnection() {

        if (!::hubConnection.isInitialized) {
            hubConnection = HubConnectionBuilder.create(hubUrl).build()
            hubConnection.onClosed {
                isConnected = false
                onDisconnected?.invoke()
                reconnectWithDelay()
            }
        }
        hubConnection.on("ReceiveMessage", { senderId: String, message: String ->
            Log.d("SignalR", "Received message: $senderId")
            Log.d("SignalR", "Received message: $message")
            onMessageReceived?.invoke(senderId, message)

        },String::class.java,String::class.java)

        hubConnection.on("UserOnline", { userId ->
            Log.d(TAG, "User online: $userId")
            onUserOnlineReceived?.invoke(userId.toInt())
        }, String::class.java)

        hubConnection.on("UserOffline", { userId ->
            Log.d(TAG, "User offline: $userId")
            onUserOfflineReceived?.invoke(userId.toInt())
        }, String::class.java)

        hubConnection.on("ReceiveOnlineUsers", { userIds: Array<String> ->
            val userList = userIds.toList()
            Log.d("SignalR", "Online users: $userList")
            val intList: List<Int> = userIds.mapNotNull { it.toIntOrNull() }
            onInitialOnlineUsers?.invoke(intList)
        }, Array<String>::class.java)

        if (!isConnected) {
            connectionDisposable = hubConnection.start()
                .doOnComplete {
                    isConnected = true
                    onConnected?.invoke()
                    println("SignalR connected")
                }
                .doOnError {
                    it.printStackTrace()
                    reconnectWithDelay()
                    println("SignalR connection error: ${it.message}")
                }
                .subscribe()
        }
    }
    private fun reconnectWithDelay() {
        Handler(Looper.getMainLooper()).postDelayed({
            initConnection()
        }, 5000)
    }

    fun disconnect() {
        if (::hubConnection.isInitialized && isConnected) {
            hubConnection.stop()
            connectionDisposable?.dispose()
            isConnected = false
        }
    }

    fun isRunning(): Boolean = isConnected
    fun sendMessage(message: String) {
        if (::hubConnection.isInitialized && hubConnection.connectionState == HubConnectionState.CONNECTED) {
            hubConnection.send("SendMessage", message)
        } else {
            Log.e(TAG, "Cannot send: SignalR not connected")
        }
    }

    fun getOnlineUser(){
        if (::hubConnection.isInitialized && hubConnection.connectionState == HubConnectionState.CONNECTED) {
            hubConnection.send("GetOnlineUsers")
        } else {
            Log.e(TAG, "Cannot send: SignalR not connected")
        }
    }

    private var onMessageReceived: ((senderId: String, message: String) -> Unit)? = null

    fun setMessageReceivedListener(listener: (String, String) -> Unit) {
        onMessageReceived = listener
    }



    private var onUserOnlineReceived: ((senderId: Int) -> Unit)? = null

    fun setonUserOnlineReceivedListener(listener: (Int) -> Unit) {
        onUserOnlineReceived = listener
    }


    private var onUserOfflineReceived: ((senderId: Int) -> Unit)? = null

    fun setonUserofflineReceivedListener(listener: (Int) -> Unit) {
        onUserOfflineReceived = listener
    }

    var onInitialOnlineUsers: ((List<Int>) -> Unit)? = null
    fun setOnlineUserListReceived(listener: (List<Int>) -> Unit) {
        onInitialOnlineUsers = listener
    }
}
