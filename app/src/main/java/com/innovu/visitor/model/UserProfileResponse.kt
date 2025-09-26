package com.innovu.visitor.model
data class UserProfileResponse(
    val success: Boolean,
    val data: UserProfile?,
    val message: String?
)


data class UserProfile(
    val userID: Int,
    val firstName: String?,
    val lastName: String?,
    val userName: String?,
    val userCode: String?,
    val password: String?,
    val userType: String?,
    val mobileNumber: String?,
    val email: String?,
    val city: String?,
    val pincode: String?,
    val state: String?,
    val address: String?,
    val gender: String?,
    val photo: String?,
    val dateOfBirth: String?,
    val joiningDate: String?,
    val shiftStartTime: String?,
    val shiftEndTime: String?,
    val status: String?,
    val employeeId: String?,
    val deviceType: String?,
    val deviceToken: String?,
    val googleToken: String?,
    val loginType: String?,
    val branchID: Int?,
    val organizationID: Int?,
    val createdAt: String?,
    val updatedAt: String?,
    val recordStatusID: Int?,
    val phoneNo: String?
)