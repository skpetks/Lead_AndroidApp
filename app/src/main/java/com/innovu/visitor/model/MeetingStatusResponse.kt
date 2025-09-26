package com.innovu.visitor.model

data class MeetingStatusResponse(
    val success: Boolean,
    val data: List<MeetingStatusData>,
    val message: String
)

data class MeetingStatusData(
    val meetingStatusID: Int,
    val meetingStatus: String,
    val meetingCount: Int
)