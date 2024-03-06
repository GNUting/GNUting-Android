package com.changs.android.gnuting_android.data.model

import com.google.gson.annotations.SerializedName

data class SignUpResponse(
    @SerializedName("result")
    val result: SignUpResult
): BaseResponse()