package com.changs.android.gnuting_android.data.model

import com.google.gson.annotations.SerializedName

data class InUser(
    @SerializedName("age")
    val age: String,
    @SerializedName("department")
    val department: String,
    @SerializedName("gender")
    val gender: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("profileImage")
    val profileImage: String? = null,
    @SerializedName("studentId")
    val studentId: String,
    @SerializedName("userRole")
    val userRole: String,
    @SerializedName("userSelfIntroduction")
    val userSelfIntroduction: String
)