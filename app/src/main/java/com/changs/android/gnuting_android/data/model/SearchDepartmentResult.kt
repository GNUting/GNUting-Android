package com.changs.android.gnuting_android.data.model

import com.google.gson.annotations.SerializedName

data class SearchDepartmentResult(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)