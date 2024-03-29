package com.changs.android.gnuting_android.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DetailPostItem(val title: String, val profile: String, val nickName: String, val department: String, val studentId: String, val detail: String, val memberNum: Int) :
    Parcelable
