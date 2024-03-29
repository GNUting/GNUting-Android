package com.changs.android.gnuting_android.data.model

data class ApplicationResponse(
    val code: String,
    val isSuccess: Boolean,
    val message: String,
    val result: List<ApplicationResult>
)