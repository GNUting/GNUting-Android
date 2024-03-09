package com.changs.android.gnuting_android.data.model

import com.google.gson.annotations.SerializedName

data class SaveResponse(
    @SerializedName("result")
    val result: String
) : BaseResponse()