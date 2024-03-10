package com.changs.android.gnuting_android.data.model

import com.google.gson.annotations.SerializedName

data class ReportRequest(
    @SerializedName("boardId")
    val boardId: Int,
    @SerializedName("reportCategory")
    val reportCategory: String,
    @SerializedName("reportReason")
    val reportReason: String
)