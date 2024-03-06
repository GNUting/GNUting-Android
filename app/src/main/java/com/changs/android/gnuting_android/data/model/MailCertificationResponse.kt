package com.changs.android.gnuting_android.data.model

import com.google.gson.annotations.SerializedName

data class MailCertificationResponse(
    @SerializedName("result")
    val result: MailCertificationResult
): BaseResponse()