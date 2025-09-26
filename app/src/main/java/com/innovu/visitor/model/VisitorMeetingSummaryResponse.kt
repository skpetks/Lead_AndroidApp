package com.innovu.visitor.model

data class VisitorMeetingSummaryResponse(
    val success: Boolean,
    val data: List<VisitorMeetingData>,
    val message: String

)

data class VisitorMeetingData(
    val Date: String,
    val Scheduled: Int,
    val Arrived: Int,
    val Approval: Int,
    val CheckIn: Int,
    val CheckoutCompelet: Int,
    val Reschedule: Int,
    val Cancelled: Int,
    val Reject: Int,
    val NotAttendent: Int
)