package com.changs.android.gnuting_android.util

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.WindowCompat
import com.changs.android.gnuting_android.GNUApplication
import com.changs.android.gnuting_android.data.model.BaseResponse
import okhttp3.ResponseBody
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun Activity.setStatusBarOrigin() {
    window.apply {
        clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }
    if (Build.VERSION.SDK_INT >= 30) {    // API 30 에 적용
        WindowCompat.setDecorFitsSystemWindows(window, true)
    }
}

fun Activity.setStatusBarTransparent() {
    window.apply {
        setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }
    if (Build.VERSION.SDK_INT >= 30) {    // API 30 에 적용
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }
}

fun View.hideSoftKeyboard() {
    val inputMethodManager = getSystemService(context, InputMethodManager::class.java)
    inputMethodManager?.hideSoftInputFromWindow(windowToken, 0)
}

fun Uri.getBitmap(contentResolver: ContentResolver): Bitmap {
    return if (Build.VERSION.SDK_INT < 28) {
        MediaStore.Images.Media.getBitmap(contentResolver, this)
    } else {
        val source = ImageDecoder.createSource(contentResolver, this)
        ImageDecoder.decodeBitmap(source)
    }
}

fun getErrorResponse(errorBody: ResponseBody): BaseResponse? {
    return GNUApplication.retrofit.responseBodyConverter<BaseResponse>(
        BaseResponse::class.java,
        BaseResponse::class.java.annotations
    ).convert(errorBody)
}

fun convertMillisecondsToTime(milliseconds: Long): String {
    val formatter = SimpleDateFormat("mm:ss", Locale.KOREA)
    formatter.timeZone = TimeZone.getTimeZone("UTC")
    return formatter.format(Date(milliseconds))
}

fun convertToKoreanTime(dateString: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
    if (dateString.contains("Z")) inputFormat.timeZone = TimeZone.getTimeZone("UTC")
    val outputFormat = SimpleDateFormat("HH:mm", Locale.KOREA)

    val parsedDate = try {
        inputFormat.parse(dateString)
    } catch (e: Exception) {
        Timber.e(e.message.toString())
        null
    }
    return parsedDate?.let { outputFormat.format(it) } ?: ""
}
