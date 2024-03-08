package com.changs.android.gnuting_android.data.model

import com.google.gson.annotations.SerializedName

data class PostResponse(
    @SerializedName("result")
    val result: List<PostResult>
) : BaseResponse()