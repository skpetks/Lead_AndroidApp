package com.innovu.visitor.model

data class GateMappingRequest(
    val gateMapID: Int,
    val gateID: Int,
    val userID: Int,
    val operationFromDate: String,  // ISO 8601 format
    val operationToDate: String,
    val operationType: String?,
    val branchID: Int?,
    val organizationID: Int?
)