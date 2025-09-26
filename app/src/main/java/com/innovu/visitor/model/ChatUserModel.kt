package com.innovu.visitor.model

data class ChatUserModel(
    val userID: Int,
    val userName: String,
    val firstName: String,
    val lastName: String?,
    val email: String?,
    val phoneNo: String?,
    val recordStatusID: Int?,
    val photo: String?
)


