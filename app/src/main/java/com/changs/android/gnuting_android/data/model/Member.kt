package com.changs.android.gnuting_android.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Member(val profile: String? = null, val name: String, val nickName: String, val studentId: String, val age: String, val mbti: String, val intro: String, val id: String, val department: String) :
    Parcelable
