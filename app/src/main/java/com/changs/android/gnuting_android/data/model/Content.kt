package com.changs.android.gnuting_android.data.model

import com.google.gson.annotations.SerializedName

data class Content(
    @SerializedName("boardId")
    val boardId: Int,
    @SerializedName("department")
    val department: String,
    @SerializedName("studentId")
    val studentId: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("inUserCount")
    val inUserCount: Int,
    @SerializedName("status")
    val status: String
)