package com.changs.android.gnuting_android.data.model

import com.changs.android.gnuting_android.base.BaseResponse
import com.google.gson.annotations.SerializedName

data class ChatDetailResponse(
    @SerializedName("result")
    val result: ChatDetailResult
): BaseResponse()