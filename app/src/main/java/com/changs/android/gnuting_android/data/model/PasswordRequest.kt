package com.changs.android.gnuting_android.data.model

import com.google.gson.annotations.SerializedName

data class PasswordRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)