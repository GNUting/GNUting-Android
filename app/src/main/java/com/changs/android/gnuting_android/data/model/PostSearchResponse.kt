package com.changs.android.gnuting_android.data.model

data class PostSearchResponse(
    val code: String,
    val isSuccess: Boolean,
    val message: String,
    val result: PostSearchResult
)