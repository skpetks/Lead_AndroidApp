package com.innovu.visitor.model

data class RescheduleStatusUpdateRequest (
    val visitorID: Int,
    val startTime: String?,         // use ISO format string e.g. "2025-06-27T12:30:00"
    val endTime: String?,
    val venue: String?,
    val operationDate: String?,
    val UserID:Int
)