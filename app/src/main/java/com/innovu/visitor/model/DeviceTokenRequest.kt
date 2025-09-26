package com.innovu.visitor.model

data class DeviceTokenRequest(
    val id: Int = 0,
    val userId: Int,
    val deviceToken: String,
    val deviceType: String = "android",
    val appVersion: String,
    val isActive: Boolean = true,
    val createdAt: String,
    val updatedAt: String,
    val deviceUID:String
)