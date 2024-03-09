package com.changs.android.gnuting_android.data.model

data class PostApplyRequestItem(
    val birthDate: String,
    val department: String,
    val gender: String,
    val id: Int,
    val nickname: String,
    val profileImage: Any,
    val userRole: String
)