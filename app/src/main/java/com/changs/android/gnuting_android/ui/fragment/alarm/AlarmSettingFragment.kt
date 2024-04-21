package com.changs.android.gnuting_android.ui.fragment.alarm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.databinding.FragmentAlarmSettingBinding


class AlarmSettingFragment : BaseFragment<FragmentAlarmSettingBinding>(
    FragmentAlarmSettingBinding::bind, R.layout.fragment_alarm_setting
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}
