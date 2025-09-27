package com.innovu.visitor.model



data class Lead(
    val leadID: Int,
    val leadDate: String,
    val leadType: String,
    val name: String,
    val phone: String,
    val email: String,
    val company: String,
    val location: String,
    val requirement: Int,
    val qty: Int,
    val rate: Int,
    val total: Int,
    val customerFeed: String,
    val organizationName: String?,
    val leadsrc: String,
    val stage: String,


)


data class PagedData(
    val totalRecords: Int,
    val pageNumber: Int,
    val pageSize: Int,
    val totalPages: Int,
    val data: List<Lead>
)

data class LeadResponse(
    val success: Boolean,
    val data: PagedData?,
    val message: String?
)