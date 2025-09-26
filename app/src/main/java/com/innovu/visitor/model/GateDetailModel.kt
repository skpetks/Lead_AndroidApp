package com.innovu.visitor.model

import com.google.gson.annotations.SerializedName



data class GateResponse(
    val success: Boolean,
    val data: List<GateDetail>,
    val message: String
)

data class GateDetail(
    val gateDetailID: Int,
    val gateName: String,
    val gateNumber: String
)