package com.changs.android.gnuting_android.data.model

import com.google.gson.annotations.SerializedName

data class LogoutRequest(
    @SerializedName("refreshToken")
    val refreshToken: String,
    @SerializedName("fcmToken")
    val fcmToken: String
)