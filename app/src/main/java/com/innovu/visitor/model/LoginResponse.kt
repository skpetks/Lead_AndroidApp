package com.innovu.visitor.model

data class LoginResponse (
    val user: UserModel?,
    val token: String
)



data class ErrorResponse(
    val error: String,
    val details: String
)