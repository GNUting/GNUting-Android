package com.changs.android.gnuting_android.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ApplicationItem(val department: String, val members: List<Member>, val status: Int) :
    Parcelable