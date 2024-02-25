package com.changs.android.gnuting_android.util

import android.content.Context
import android.util.AttributeSet
import android.widget.Checkable
import android.widget.LinearLayout
import androidx.annotation.AttrRes
import com.changs.android.gnuting_android.R


class CheckableLinearLayout
@JvmOverloads() constructor(
    context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), Checkable {
    private var mChecked = false

    override fun setChecked(checked: Boolean) {
        mChecked = checked
        updateChecked()
    }

    override fun isChecked(): Boolean {
        return mChecked
    }

    override fun toggle() {
        isChecked = !mChecked
    }

    private fun updateChecked() {
        if (mChecked) {
            setBackgroundResource(R.drawable.background_radius_10dp_stroke_secondary)
        } else {
            setBackgroundResource(R.drawable.background_radius_10dp_stroke_gray7)
        }
    }
}