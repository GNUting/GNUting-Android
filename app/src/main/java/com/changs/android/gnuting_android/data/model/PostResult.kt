package com.changs.android.gnuting_android.data.model

import com.google.gson.annotations.SerializedName

data class PostResult(
    @SerializedName("detail")
    val detail: String,
    @SerializedName("gender")
    val gender: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("inUserCount")
    val inUserCount: Int,
    @SerializedName("status")
    val status: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("user")
    val user: User
)