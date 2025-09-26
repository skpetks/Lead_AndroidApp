package com.innovu.visitor.model

data class ChatItem(
    val userId: String,
    val userName: String,
    val lastMessage: String,
    val timestamp: String,
    val profileImageUrl: String? = null
)
