package com.innovu.visitor.ui.visitorcreate

data class Student(
    val studentID: Int,
    val organizationID: Int,
    val branchID: Int,
    val grNo: String,
    val firstName: String,
    val lastName: String,
    val gender: String,
    val dateOfBirth: String,
    val `class`: String,
    val section: String,
    val admissionDate: String,
    val image: String?,
    val fatherName: String,
    val motherName: String,
    val guardianName: String,
    val address: String,
    val city: String,
    val state: String,
    val contactNumber: String,
    val email: String,
    val recordStatusID: Int?,
    val createdAt: String,
    val updatedAt: String
)
