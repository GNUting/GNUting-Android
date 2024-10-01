package com.changs.android.gnuting_android.data.model

import com.google.gson.annotations.SerializedName

data class ChatListResult2(
    @SerializedName("applyLeaderDepartment")
    val applyLeaderDepartment: String,
    @SerializedName("chatRoomUserProfileImages")
    val chatRoomUserProfileImages: List<String?>,
    @SerializedName("chatRoomUsers")
    val chatRoomUsers: List<ChatRoomUser?>,
    @SerializedName("hasNewMessage")
    val hasNewMessage: Boolean,
    @SerializedName("id")
    val id: Int,
    @SerializedName("lastMessage")
    val lastMessage: String,
    @SerializedName("lastMessageTime")
    val lastMessageTime: String,
    @SerializedName("leaderUserDepartment")
    val leaderUserDepartment: String,
    @SerializedName("title")
    val title: String
)