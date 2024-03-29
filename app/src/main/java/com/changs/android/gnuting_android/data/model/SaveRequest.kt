package com.changs.android.gnuting_android.data.model

import com.google.gson.annotations.SerializedName

data class SaveRequest(
    @SerializedName("detail")
    val detail: String,
    @SerializedName("inUser")
    val inUser: List<InUser>,
    @SerializedName("title")
    val title: String
)