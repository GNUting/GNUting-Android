package com.changs.android.gnuting_android.data.model

import com.google.gson.annotations.SerializedName

open class BaseResponse {
    @SerializedName("isSuccess")
    val isSuccess: Boolean? = null

    @SerializedName("code")
    val code: String? = null

    @SerializedName("message")
    val message: String? = null
}