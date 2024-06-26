package com.changs.android.gnuting_android.data.model

import com.google.gson.annotations.SerializedName

data class ChatListResult(
    @SerializedName("applyLeaderDepartment")
    val applyLeaderDepartment: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("hasNewMessage")
    val hasNewMessage: Boolean,
    @SerializedName("leaderUserDepartment")
    val leaderUserDepartment: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("chatRoomUserProfileImages")
    val chatRoomUserProfileImages: List<String>,
    @SerializedName("chatRoomUsers")
    val chatRoomUsers: List<ChatRoomUser>
)