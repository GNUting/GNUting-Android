package com.changs.android.gnuting_android.data.model

import com.google.gson.annotations.SerializedName

data class ApplicationDetailResult(
    @SerializedName("applyStatus")
    val applyStatus: String,
    @SerializedName("applyUser")
    val applyUser: List<InUser>,
    @SerializedName("applyUserCount")
    val applyUserCount: Int,
    @SerializedName("applyUserDepartment")
    val applyUserDepartment: String,
    @SerializedName("createdDate")
    val createdDate: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("modifiedDate")
    val modifiedDate: String,
    @SerializedName("participantUser")
    val participantUser: List<InUser>,
    @SerializedName("participantUserCount")
    val participantUserCount: Int,
    @SerializedName("participantUserDepartment")
    val participantUserDepartment: String
)