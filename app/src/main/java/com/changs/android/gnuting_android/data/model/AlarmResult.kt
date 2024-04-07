package com.changs.android.gnuting_android.data.model

import com.google.gson.annotations.SerializedName

data class AlarmResult(
    @SerializedName("body")
    val body: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("time")
    val time: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("status")
    val status: String
)