package com.changs.android.gnuting_android.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ApplicationResult(
    @SerializedName("applyStatus")
    val applyStatus: String,
    @SerializedName("applyUser")
    val applyUser: List<InUser>,
    @SerializedName("applyUserCount")
    val applyUserCount: Int,
    @SerializedName("applyUserDepartment")
    val applyUserDepartment: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("participantUser")
    val participantUser: List<InUser>,
    @SerializedName("participantUserCount")
    val participantUserCount: Int,
    @SerializedName("participantUserDepartment")
    val participantUserDepartment: String
): Parcelable