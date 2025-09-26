package com.innovu.visitor.model

import android.os.Parcelable

data class VisitorResponse(
    val success: Boolean,
    val data: VisitorDataWrapper?,
    val message: String?
)

data class VisitorDataWrapper(
    val totalRecords: Int,
    val data: List<Visitor>
)


data class Visitor(
    val visitorID: Int,
    val visitorCode: String,
    val visitorName: String,
    val studentID: Int,
    val visitorTypeID: Int,
    val startTime: String,
    val endTime: String,
    val venue: String?,
    val checkINtime: String?,
    val checkOutTime: String?,
    val meetingTypeID: Int,
    val parentContact: String,
    val checkinUserID: Int?,
    val checkoutUserID: Int?,
    val operationDate: String,
    val smsStatus: String?,
    val smsResponse: String?,
    val recordStatusID: Int,
    val meetingUserID: Int,
    val note: String,
    val branchID: Int,
    val organizationID: Int,
    val inGateID: Int?,
    val outGateID: Int?,
    val createdAt: String,
    val updatedAt: String,
    val cancelUserID: Int?,
    val cancelDatetime: String?,
    val qrPath: String?,
    val userName: String?,
    val visitorType: String?,
    val staffPhone: String?,
    val department: String?,
    val fatherName: String?,
    val motherName: String?,
    val GuardianName: String?,
    val contactNumber: String?,
    val recordStatus: String?,
    val meetingType:String?,
    val studentName:String?,
    val classname:String?,
    val section:String?,
    val grno:String?,
    val departmentID: Int?,
    val image: List<ImageModel>
)




data class VisitorFilterResponse(
    val success: Boolean,
    val data:  List<Visitor>?,
    val message: String?
)


data class VisitorCodeResponse(
    val success: Boolean,
    val data: Visitor?,
    val message: String?
)
