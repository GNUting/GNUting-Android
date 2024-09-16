package com.changs.android.gnuting_android.data.model

import com.google.gson.annotations.SerializedName

data class PostMemoRequestBody(
    @SerializedName("content")
    val content: String
)