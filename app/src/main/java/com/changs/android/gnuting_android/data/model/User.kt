package com.changs.android.gnuting_android.data.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("department")
    val department: String,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("studentId")
    val studentId: String,
    @SerializedName("image")
    val profileImage: String? = null
)