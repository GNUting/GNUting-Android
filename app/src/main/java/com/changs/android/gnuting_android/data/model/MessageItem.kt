package com.changs.android.gnuting_android.data.model

import com.google.gson.annotations.SerializedName

data class MessageItem(
    @SerializedName("chatRoomId")
    val chatRoomId: Int,
    @SerializedName("createdDate")
    val createdDate: String? = null,
    @SerializedName("email")
    val email: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("messageType")
    val messageType: String,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("profileImage")
    val profileImage: String
)