package com.changs.android.gnuting_android.data.model

import com.google.gson.annotations.SerializedName

data class NotificationSettingRequest(@SerializedName("notificationSetting") val notificationSetting: String)