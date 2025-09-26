package com.innovu.visitor.model

data class ImageModel(
    val imageID: Int,
    val organizationID: Int,
    val imagePath: String,
    val visitorID: Int,
    val uploadedAt: String,
    val uploadedBy: Int,
    val description: String?,
    val status: String?,
    val createdAt: String,
    val updatedAt: String
)