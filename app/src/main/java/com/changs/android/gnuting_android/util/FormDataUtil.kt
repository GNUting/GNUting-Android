package com.changs.android.gnuting_android.util

import android.graphics.Bitmap
import okhttp3.MultipartBody


object FormDataUtil {
    fun getTextBody(key: String, value: String): MultipartBody.Part {
        return MultipartBody.Part.createFormData(key, value)
    }

    fun getImageBody(
        bitmap: Bitmap?, name: String = "", fileName: String = ""
    ): MultipartBody.Part? {
        val bitmapRequestBody = bitmap?.let { BitmapRequestBody(it) }

        return if (bitmapRequestBody == null) null
        else MultipartBody.Part.createFormData(name, fileName, bitmapRequestBody)
    }
}