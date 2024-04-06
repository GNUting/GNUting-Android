package com.changs.android.gnuting_android.data.model

import com.google.gson.annotations.SerializedName

data class MailCertificationRequest(
    @SerializedName("email")
    val email: String
)