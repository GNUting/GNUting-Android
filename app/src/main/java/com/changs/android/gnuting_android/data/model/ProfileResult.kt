package com.changs.android.gnuting_android.data.model

import com.google.gson.annotations.SerializedName

data class ProfileResult(
    @SerializedName("birthDate")
    val birthDate: String,
    @SerializedName("department")
    val department: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("gender")
    val gender: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("phoneNumber")
    val phoneNumber: String,
    @SerializedName("profileImage")
    val profileImage: String,
    @SerializedName("studentId")
    val studentId: String,
    @SerializedName("userRole")
    val userRole: String,
    @SerializedName("userSelfIntroduction")
    val userSelfIntroduction: String
)