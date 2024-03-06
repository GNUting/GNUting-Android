package com.changs.android.gnuting_android.data.model

import com.google.gson.annotations.SerializedName

data class SearchDepartmentResponse(
    @SerializedName("result") val result: List<SearchDepartmentResult>
) : BaseResponse()