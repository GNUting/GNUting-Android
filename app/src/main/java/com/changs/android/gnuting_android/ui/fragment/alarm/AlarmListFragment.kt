package com.changs.android.gnuting_android.ui.fragment.alarm

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.changs.android.gnuting_android.GNUApplication
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.databinding.FragmentAlarmListBinding
import com.changs.android.gnuting_android.ui.MainActivity
import com.changs.android.gnuting_android.ui.adapter.AlarmAdapter
import com.changs.android.gnuting_android.util.eventObserve
import com.changs.android.gnuting_android.util.showTwoButtonDialog
import com.changs.android.gnuting_android.viewmodel.AlarmViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlarmListFragment : BaseFragment<FragmentAlarmListBinding>(
    FragmentAlarmListBinding::bind, R.layout.fragment_alarm_list
) {
    private val viewModel: AlarmViewModel by viewModels()
    private val adapter: AlarmAdapter by lazy {
        AlarmAdapter {
            showTwoButtonDialog(requireContext(), "알림을 삭제하시겠습니까?", rightButtonText = "삭제") {
                viewModel.deleteAlarm(it)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getAlarmList()
        setRecyclerView()
        setListener()
        setObserver()
    }

    private fun setListener() {
        binding.alarmListImgClose.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setObserver() {
        viewModel.alarmListResponse.observe(viewLifecycleOwner) {
            adapter.submitList(it.result)
        }

        viewModel.deleteAlarmResponse.observe(viewLifecycleOwner) {
            viewModel.getAlarmList()
        }

        viewModel.expirationToken.eventObserve(viewLifecycleOwner) {
            GNUApplication.sharedPreferences.edit().clear().apply()
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        viewModel.spinner.observe(viewLifecycleOwner) { show ->
            binding.spinner.visibility = if (show) View.VISIBLE else View.GONE
        }

        viewModel.toast.eventObserve(viewLifecycleOwner) { text ->
            text?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setRecyclerView() {
        binding.alarmListRecycler.adapter = adapter
        binding.alarmListRecycler.itemAnimator = null
    }
}