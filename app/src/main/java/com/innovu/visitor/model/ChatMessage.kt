package com.innovu.visitor.model

import com.google.gson.JsonElement

data class ChatMessage(
    val chatID: Int,
    var message: String,
    val sentAt: String,
    val readAt: String?,
    val senderID: Int,
    val senderUserName: String,
    val senderFirstName: String,
    val receiverID: Int,
    val receiverUserName: String,
    val receiverFirstName: String,
    val attachmentPath: String?,
    val isSentByMe: Boolean,
)


data class ChatResponse(
    val success: Boolean,
    val data: JsonElement?,//List<ChatMessage>
    val message: String
)