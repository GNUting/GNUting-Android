package com.changs.android.gnuting_android.ui.fragment.alarm

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.databinding.FragmentAlarmListBinding
import com.changs.android.gnuting_android.ui.HomeActivity
import com.changs.android.gnuting_android.ui.compose.AlarmList
import com.changs.android.gnuting_android.util.eventObserve
import com.changs.android.gnuting_android.util.showTwoButtonDialog
import com.changs.android.gnuting_android.viewmodel.AlarmViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
@AndroidEntryPoint
class AlarmListFragment : BaseFragment<FragmentAlarmListBinding>(
    FragmentAlarmListBinding::bind, R.layout.fragment_alarm_list
) {
    private val viewModel: AlarmViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getAlarmList()
        setListener()
        setObserver()
    }

    private fun setListener() {
        binding.alarmListImgClose.setOnClickListener {
            findNavController().popBackStack()
        }

/*        binding.alarmListRefresh.setColorSchemeColors(resources.getColor(R.color.main, null))
        binding.alarmListRefresh.setOnRefreshListener {
            viewModel.getAlarmList()
        }*/
    }

    private fun setObserver() {
        viewModel.alarmListResponse.observe(viewLifecycleOwner) {
            // if (binding.alarmListRefresh.isRefreshing) binding.alarmListRefresh.isRefreshing = false

            binding.alarmComposeView.setContent {
                AlarmList(data = it.result, onClick = ::navigateListener, onLongClick = {
                    showTwoButtonDialog(requireContext(), "알림을 삭제하시겠습니까?", rightButtonText = "삭제") {
                        viewModel.deleteAlarm(it)
                    }
                })
            }

            if (it.result.isNotEmpty()) {
                binding.alarmListLlEmpty.visibility = View.GONE
            } else {
                binding.alarmListLlEmpty.visibility = View.VISIBLE
            }
        }

        viewModel.deleteAlarmResponse.observe(viewLifecycleOwner) {
            viewModel.getAlarmList()
        }

        viewModel.spinner.observe(viewLifecycleOwner) { show ->
            binding.spinner.visibility = if (show) View.VISIBLE else View.GONE
        }

        viewModel.toast.eventObserve(viewLifecycleOwner) { text ->
            text?.let {
                (requireActivity() as HomeActivity).showToast(it)
            }
        }
    }

    private fun navigateListener() {
        findNavController().navigate(R.id.action_alarmListFragment_to_listFragment2)
    }

    override fun onPause() {
        super.onPause()
        // if (binding.alarmListRefresh.isRefreshing) binding.alarmListRefresh.isRefreshing = false
    }
}