package com.innovu.visitor.model

data class OtpRequest(
    val phoneNumber: String,
    val userId: Int,
    val organizationId: Int,
    val branchId: Int
)


data class OtpResponse(
    val success: Boolean,
    val data: OtpData?,
    val message: String
)

data class OtpData(
    val phoneNumber: String,
    val otp: String
)