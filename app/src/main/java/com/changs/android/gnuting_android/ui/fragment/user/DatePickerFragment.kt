package com.changs.android.gnuting_android.ui.fragment.user

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import com.changs.android.gnuting_android.R
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {
    lateinit var listener: (Int, Int, Int) -> Unit
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(
            requireActivity(), R.style.SpinnerDatePickerDialogStyle, this, year, month, day
        )
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        listener(year, month + 1, day)
    }
}