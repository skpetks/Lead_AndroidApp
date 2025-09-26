package com.innovu.visitor.model

data class ChatPostRequest(
    val chatID: Int,
    val organizationID: Int,
    val senderID: Int,
    val receiverID: Int,
    val isActive: Boolean,
    val message: String,
    val attachmentPath: String,
    val sentAt: String,
    val readAt: String,
    val createdAt: String,
    val updatedAt: String
)