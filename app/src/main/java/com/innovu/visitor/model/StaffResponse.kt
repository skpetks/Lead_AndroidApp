package com.innovu.visitor.model

data class StaffResponse(
    val success: Boolean,
    val data: List<ChatUserModel>,
    val message: String?
)