package com.changs.android.gnuting_android.data.model

import com.google.gson.annotations.SerializedName

data class UserReportRequest(
    @SerializedName("nickName")
    val nickName: String,
    @SerializedName("reportCategory")
    val reportCategory: String,
    @SerializedName("reportReason")
    val reportReason: String
)