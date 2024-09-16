package com.changs.android.gnuting_android.data.model

import com.google.gson.annotations.SerializedName

data class MemoResult(
    @SerializedName("content")
    val content: String,
    @SerializedName("gender")
    val gender: String,
    @SerializedName("id")
    val id: Int
)