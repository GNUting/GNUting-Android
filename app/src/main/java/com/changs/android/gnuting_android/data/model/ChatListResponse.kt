package com.changs.android.gnuting_android.data.model

import com.changs.android.gnuting_android.base.BaseResponse
import com.google.gson.annotations.SerializedName

data class ChatListResponse(
    @SerializedName("result")
    val result: List<ChatListResult>
) : BaseResponse()