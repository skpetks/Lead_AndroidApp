package com.innovu.visitor.model

data class SearchRequest(
    val OrganizationID: Int,
    val BranchID: Int,
    val keyword: String
)
data class SearchRequestResponse(

    val message: String
)
