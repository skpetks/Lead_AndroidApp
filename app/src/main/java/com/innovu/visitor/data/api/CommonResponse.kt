package com.innovu.visitor.data.api

data class CommonResponse(
    val statuscode: Int,
    val status: String,
    val result: String,
    val message: String
)