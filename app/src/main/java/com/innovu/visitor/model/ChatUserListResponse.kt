package com.innovu.visitor.model

data class ChatUserListResponse(
    val success: Boolean,
    val data: List<ChatUser>,
    val message: String
)

data class ChatUser(
    val userID: Int,
    val firstName: String,
    val lastName: String,
    val userName: String,
    val email: String,
    val photo: String,
    val message: String,
    val sentAt: String, // or use `LocalDateTime` with a converter if needed,
    var isOnline: Boolean = false,
)
