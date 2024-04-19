package com.changs.android.gnuting_android.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    @SerializedName("department")
    val department: String,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("studentId")
    val studentId: String,
    @SerializedName("image")
    val profileImage: String? = null
) : Parcelable