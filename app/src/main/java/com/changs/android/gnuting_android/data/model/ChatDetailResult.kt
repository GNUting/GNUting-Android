package com.changs.android.gnuting_android.data.model

import com.google.gson.annotations.SerializedName

data class ChatDetailResult(
    @SerializedName("applyLeaderDepartment")
    val applyLeaderDepartment: String,
    @SerializedName("leaderUserDepartment")
    val leaderUserDepartment: String,
    @SerializedName("title")
    val title: String
)