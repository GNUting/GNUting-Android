package com.changs.android.gnuting_android.data.model

import com.google.gson.annotations.SerializedName

data class EmailVerifyRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("number")
    val number: String
)