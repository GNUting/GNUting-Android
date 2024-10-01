package com.changs.android.gnuting_android.data.model

import com.google.gson.annotations.SerializedName

data class PostEventRequestBody(
    @SerializedName("nickname")
    val nickname: String
)