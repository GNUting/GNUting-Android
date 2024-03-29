package com.changs.android.gnuting_android.data.model

import com.google.gson.annotations.SerializedName

data class SignUpResult(
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("refreshToken")
    val refreshToken: String
)