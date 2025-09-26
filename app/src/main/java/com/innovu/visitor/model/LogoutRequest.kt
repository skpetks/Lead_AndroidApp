package com.innovu.visitor.model

data class LogoutRequest(
    val userId: Int,
    val deviceType: String,
    val deviceUID: String
)
data class LogoutRequestresponse(

    val message: String
)
