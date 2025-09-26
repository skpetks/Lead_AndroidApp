package com.innovu.visitor.model

data class CheckInRequest (
    val visitorID: Int,
    val CheckINtime: String?,
    val CheckinUserID: Int,
    val InGateID:Int
)
data class CheckOutRequest (
    val visitorID: Int,
    val CheckOutTime: String?,
    val CheckoutUserID: Int,
    val OutGateID:Int
)
