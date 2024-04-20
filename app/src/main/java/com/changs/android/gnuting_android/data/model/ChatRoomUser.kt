package com.changs.android.gnuting_android.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChatRoomUser(
    @SerializedName("chatRoomId")
    val chatRoomId: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("profileImage")
    val profileImage: String,
    @SerializedName("userId")
    val userId: Int
) : Parcelable