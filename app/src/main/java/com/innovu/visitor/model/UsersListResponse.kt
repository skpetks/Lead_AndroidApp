package com.innovu.visitor.model

data class UsersListResponse(
    val success: Boolean,
    val data: List<User>,
    val message: String
)

data class User(
    val userID: Int,
    val firstName: String?,
    val lastName: String?,
    val userName: String?,
    val userCode: String?,
    val password: String?,
    val userType: String?,
    val mobileNumber: String?,
    val email: String?,
    val departmentID:Int

)
