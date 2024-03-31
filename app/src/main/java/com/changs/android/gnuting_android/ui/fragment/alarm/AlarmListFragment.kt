package com.changs.android.gnuting_android.ui.fragment.alarm

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.databinding.FragmentAlarmListBinding
import com.changs.android.gnuting_android.ui.adapter.AlarmAdapter
import com.changs.android.gnuting_android.util.showTwoButtonDialog
import com.changs.android.gnuting_android.viewmodel.HomeMainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
@AndroidEntryPoint
class AlarmListFragment : BaseFragment<FragmentAlarmListBinding>(
    FragmentAlarmListBinding::bind, R.layout.fragment_alarm_list
) {
    private val viewModel: HomeMainViewModel by activityViewModels()
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
    }

    private fun setRecyclerView() {
        binding.alarmListRecycler.adapter = adapter
        binding.alarmListRecycler.itemAnimator = null
    }
}