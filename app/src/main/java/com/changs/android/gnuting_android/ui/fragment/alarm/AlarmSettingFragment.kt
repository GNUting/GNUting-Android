package com.changs.android.gnuting_android.ui.fragment.alarm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.data.model.AlarmStatus
import com.changs.android.gnuting_android.data.model.NotificationSettingRequest
import com.changs.android.gnuting_android.databinding.FragmentAlarmSettingBinding
import com.changs.android.gnuting_android.viewmodel.AlarmViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AlarmSettingFragment : BaseFragment<FragmentAlarmSettingBinding>(
    FragmentAlarmSettingBinding::bind, R.layout.fragment_alarm_setting
) {
    private val viewModel: AlarmViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getNotificationStatus()
        setObserver()

        binding.alarmSettingImgBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setObserver() {
        viewModel.alarmStatusResponse.observe(viewLifecycleOwner) {
            when (it.result.notificationSetting) {
                AlarmStatus.ENABLE.name -> {
                    binding.alarmSettingSwitch.isChecked = true
                }

                AlarmStatus.DISABLE.name -> {
                    binding.alarmSettingSwitch.isChecked = false
                }
            }

            binding.alarmSettingSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                val requestSettingStatus =
                    if (isChecked) AlarmStatus.ENABLE.name else AlarmStatus.DISABLE.name

                viewModel.putNotificationSetting(NotificationSettingRequest(requestSettingStatus))
            }
        }
    }
}
