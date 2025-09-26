package com.innovu.visitor.model

data class MeetingTypeResponse(
    val success: Boolean,
    val data: List<MeetingType>,
    val message: String
)

data class MeetingType(
    val meetingTypeID: Int,
    val meetingName: String,
    val description: String,
    val recordStatusID: Int,
    val organizationID: Int,
    val organization: String,
    val branch: String,
    val branchID: Int,
    val createdAt: String,
    val updatedAt: String,
    val recordStatus: String
)
