package com.changs.android.gnuting_android.data.model

import com.google.gson.annotations.SerializedName

data class LoginResult(
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("refreshToken")
    val refreshToken: String
)