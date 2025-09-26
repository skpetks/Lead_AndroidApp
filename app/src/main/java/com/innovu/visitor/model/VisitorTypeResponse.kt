package com.innovu.visitor.model

data class VisitorTypeResponse(
    val success: Boolean,
    val data: List<VisitorType>,
    val message: String
)

data class VisitorType(
    val visitorTypeID: Int,
    val visitorTypeDetail: String
)


data class ApiResponse<T>(
    val success: Boolean,
    val data: T?,
    val message: String
)