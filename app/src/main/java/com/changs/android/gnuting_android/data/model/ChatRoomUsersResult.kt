package com.changs.android.gnuting_android.data.model

import com.google.gson.annotations.SerializedName

data class ChatRoomUsersResult(
    @SerializedName("chatRoomId")
    val chatRoomId: Int,
    @SerializedName("department")
    val department: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("profileImage")
    val profileImage: String,
    @SerializedName("studentId")
    val studentId: String,
    @SerializedName("userId")
    val userId: Int
)