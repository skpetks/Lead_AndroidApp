package com.innovu.visitor.model

data class DepartmentResponse(
    val success: Boolean,
    val data: List<Department>,
    val message: String
)

data class Department(
    val departmentID: Int,
    val departmentName: String,
    val branchID: Int,
    val organizationID: Int,
    val departmentStatus: Int
)