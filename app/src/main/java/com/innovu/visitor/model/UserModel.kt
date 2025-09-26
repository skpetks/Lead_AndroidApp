package com.innovu.visitor.model

import com.google.gson.annotations.SerializedName

data class UserModel(
    @SerializedName("UserName")
    var UserName : String,
    @SerializedName("UserID")
    var UserID   : Int ,
    @SerializedName("Email")
    var Email    : String ,
    @SerializedName("Role")
    var Role     : String ,
    @SerializedName("RoleId")
    var RoleId   : Int ,
    @SerializedName("BranchID")
    var BranchID   : Int ,
    @SerializedName("OrganizationID")
    var OrganizationID   : Int ,
    @SerializedName("GateID")
    var GateID   : Int ,
    @SerializedName("DepartmentID")
    var DepartmentID   : Int ,


)


data class UserListResponse (
    val statuscode: Int,
    val status: String,
    val result: List<UserModel>,
    val message: String
)