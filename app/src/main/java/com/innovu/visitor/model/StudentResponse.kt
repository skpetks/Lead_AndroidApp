package com.innovu.visitor.model

import com.innovu.visitor.ui.visitorcreate.Student

data class StudentResponse(
    val success: Boolean,
    val data: List<Student>,
    val message: String
)


