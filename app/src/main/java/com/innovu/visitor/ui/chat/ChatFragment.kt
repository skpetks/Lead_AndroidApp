package com.innovu.visitor.ui.chat

import android.graphics.Color
import android.media.RingtoneManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.innovu.visitor.BuildConfig
import com.innovu.visitor.MainActivity
import com.innovu.visitor.R
import com.innovu.visitor.model.ChatMessage
import com.innovu.visitor.services.SignalRManager
import com.innovu.visitor.utlis.StorePrefData
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import io.reactivex.rxjava3.disposables.Disposable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.Int
import kotlin.getValue

class ChatFragment : Fragment() {

    private lateinit var hubConnection: HubConnection
    private var connectionDisposable: Disposable? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChatAdapter
    private lateinit var editMessage: androidx.appcompat.widget.AppCompatEditText
    private lateinit var buttonSend: AppCompatImageButton
    private  var RecevierId: String = ""
    private val chatMessages = mutableListOf<ChatMessage>()
    private val chatViewModel: ChatViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        RecevierId = arguments?.getString("userId").toString()
        val userName = arguments?.getString("userName")

        (activity as AppCompatActivity).supportActionBar?.title ="Chat with $userName"

        recyclerView = view.findViewById(R.id.recyclerMessages)
        editMessage = view.findViewById(R.id.editMessage)
        buttonSend = view.findViewById(R.id.buttonSend)

        adapter = ChatAdapter()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        buttonSend.setOnClickListener {
            val messageText = editMessage.text.toString().trim()
            if (messageText.isNotEmpty()) {
            //    chatMessages.add(ChatMessage(messageText, true))

                val nowUtc: String = getCurrentDateTime()
                val chatMessage = ChatMessage(
                    chatID = 0,
                    message = messageText,
                    sentAt = nowUtc,
                    readAt = nowUtc,
                    senderID = StorePrefData.UserIId,
                    senderUserName = "Sadmin",
                    senderFirstName = "Satcop",
                    receiverID = RecevierId.toInt(),
                    receiverUserName = "User3",
                    receiverFirstName = "John",
                    attachmentPath = null,
                    isSentByMe = true,
                )

                editMessage.text?.clear()
                adapter.addMessage(chatMessage)
                recyclerView.scrollToPosition(adapter.itemCount - 1)

                (recyclerView.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false

                trySendMessage(  messageText,5)

            }
        }


        chatViewModel.chatList.observe(viewLifecycleOwner) { chatlist ->
            // Update your RecyclerView adapter
            adapter.updateData(chatlist)
    if (chatlist.size>0) {
        recyclerView.scrollToPosition(adapter.itemCount - 1)

        (recyclerView.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
    }

        }

        chatViewModel.error.observe(viewLifecycleOwner) {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()

            Log.d("error", "Message error: $it")
        }

// Call API
        chatViewModel.fetChatMessage(RecevierId.toInt())
        editMessage.requestFocus()
        SignalRManager.setMessageReceivedListener { senderId, message ->
                Log.d("ChatActivity", "New message from $senderId: $message")
                // chatAdapter.addMessage(...) or update your ViewModel
            requireActivity().runOnUiThread {
                Log.d("ReceiveMessage", "ReceiveMessage started");
                if(senderId==RecevierId)
                {
                    val nowUtc: String = getCurrentDateTime()
                    val chatMessage = ChatMessage(
                        chatID = 101,
                        message = message,
                        sentAt = nowUtc,
                        readAt = nowUtc,
                        senderID = RecevierId.toInt(),
                        senderUserName = "Sadmin",
                        senderFirstName = "Satcop",
                        receiverID = senderId.toInt(),
                        receiverUserName = "User3",
                        receiverFirstName = "John",
                        attachmentPath = null,
                        isSentByMe = false,
                    )
                    try {
                        val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                        val r = RingtoneManager.getRingtone(requireContext(), notification)
                        r.play()
                    } catch (e: Exception) {
                        Log.e("SoundError", "Error playing sound: ${e.message}")
                    }
                    adapter.addMessage(chatMessage)
                    recyclerView.scrollToPosition(adapter.itemCount - 1)
                }
            }
        }
        (requireActivity() as AppCompatActivity).let {
            if (it is MainActivity) {
                it.updateTitleWithColor("$userName", Color.BLACK)
            }
        }
        (requireActivity() as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.arrow_left)
    }
    private fun chathubConnection() {
        var uid= StorePrefData.UserID;
        val replaceurl = BuildConfig.SERVICE_END_POINT.replace("/api/", "/")
        var chaturl=replaceurl+"chathub?userId="+uid

       HubConnectionBuilder
            .create(chaturl) // Replace with your actual URL
            .build()
        hubConnection =   HubConnectionBuilder
            .create(chaturl)
            .build()
        // Handle when other user becomes online
        hubConnection.on("UserOnline", { onlineUserId ->
            Log.d("SignalR", "$onlineUserId is online")
            // Update UI to show user is online
        }, String::class.java)
        // Handle when other user goes offline
        hubConnection.on("UserOffline", { offlineUserId ->
            Log.d("SignalR", "$offlineUserId is offline")
            // Update UI to show user is offline
        }, String::class.java)
        // Listen for incoming messages
        hubConnection.on("ReceiveMessage", { senderId: String, message: String ->
            Log.d("SignalR", "Received message: $senderId")
            Log.d("SignalR", "Received message: $message")
            requireActivity().runOnUiThread {
                Log.d("ReceiveMessage", "ReceiveMessage started");
                if(senderId==RecevierId)
                {
                    val nowUtc: String = getCurrentDateTime()
                    val chatMessage = ChatMessage(
                        chatID = 101,
                        message = message,
                        sentAt = nowUtc,
                        readAt = nowUtc,
                        senderID = RecevierId.toInt(),
                        senderUserName = "Sadmin",
                        senderFirstName = "Satcop",
                        receiverID = senderId.toInt(),
                        receiverUserName = "User3",
                        receiverFirstName = "John",
                        attachmentPath = null,
                        isSentByMe = false,
                    )
                    try {
                        val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                        val r = RingtoneManager.getRingtone(requireContext(), notification)
                        r.play()
                    } catch (e: Exception) {
                        Log.e("SoundError", "Error playing sound: ${e.message}")
                    }
                    adapter.addMessage(chatMessage)
                    recyclerView.scrollToPosition(adapter.itemCount - 1)
                    }
            }
        },String::class.java,String::class.java)

        hubConnection.onClosed { error ->
            Log.e("SignalR", "Connection closed with error: ${error?.message}", error)
        }

        // Start the connection
        connectionDisposable = hubConnection.start()
            .doOnComplete {
                Log.d("SignalR", "Connection started")
            }
            .doOnError { error ->
                Log.e("SignalR", "Connection failed: ${error.message}")
            }
            .subscribe()
    }

    private fun trySendMessage( message: String, retryCount: Int = 0) {
        chatViewModel.postChatMessage(RecevierId.toInt(),message)
        if (::hubConnection.isInitialized && hubConnection.connectionState == HubConnectionState.CONNECTED) {
      //      hubConnection.send("SingleSendMessage", StorePrefData.UserID,RecevierId, message)
            chatViewModel.postChatMessage(RecevierId.toInt(),message)
            Log.d("SignalR", "Message SendMessage2: $message")
        } else if (retryCount < 5) {
            Log.w("SignalR", "Connection not ready. Retrying in 1 sec...")
            Handler(Looper.getMainLooper()).postDelayed({
                trySendMessage(message, retryCount + 1)
            }, 1000)
        } else {
            Log.e("SignalR", "Failed to send message after retries.")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disconnectSignalR()
    }


    fun getCurrentDateTime(): String {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        return current.format(formatter)
    }

    private fun disconnectSignalR() {
        connectionDisposable?.dispose()
        if (::hubConnection.isInitialized && hubConnection.connectionState == HubConnectionState.CONNECTED) {
            hubConnection.stop()
        }
    }
}


