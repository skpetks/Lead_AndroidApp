package com.innovu.visitor.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("username")
    private val username: String,
    @SerializedName("password")
    private val password: String,
    @SerializedName("email")
    private val email: String,

    )
