package com.changs.android.gnuting_android.ui.fragment.alarm

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.databinding.FragmentAlarmListBinding
import com.changs.android.gnuting_android.ui.HomeActivity
import com.changs.android.gnuting_android.ui.adapter.AlarmAdapter
import com.changs.android.gnuting_android.ui.adapter.ApplicationAdapter
import com.changs.android.gnuting_android.util.eventObserve
import com.changs.android.gnuting_android.util.showTwoButtonDialog
import com.changs.android.gnuting_android.viewmodel.AlarmViewModel
import com.changs.android.gnuting_android.viewmodel.HomeMainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
@AndroidEntryPoint
class AlarmListFragment : BaseFragment<FragmentAlarmListBinding>(
    FragmentAlarmListBinding::bind, R.layout.fragment_alarm_list
) {
    private val alarmViewModel: AlarmViewModel by viewModels()
    private val viewModel: HomeMainViewModel by activityViewModels()
    private val adapter: AlarmAdapter by lazy {
        AlarmAdapter(::navigateListener) {
            showTwoButtonDialog(requireContext(), "알림을 삭제하시겠습니까?", rightButtonText = "삭제") {
                alarmViewModel.deleteAlarm(it)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        alarmViewModel.getAlarmList()
        setRecyclerView()
        setListener()
        setObserver()
    }

    private fun setListener() {
        binding.alarmListImgClose.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.alarmListRefresh.setColorSchemeColors(resources.getColor(R.color.main, null))
        binding.alarmListRefresh.setOnRefreshListener {
            alarmViewModel.getAlarmList()
        }
    }

    private fun setObserver() {
        alarmViewModel.alarmListResponse.observe(viewLifecycleOwner) {
            if (binding.alarmListRefresh.isRefreshing) binding.alarmListRefresh.isRefreshing = false
            adapter.submitList(it.result)

            if (it.result.isNotEmpty()) {
                binding.alarmListLlEmpty.visibility = View.GONE
            } else {
                binding.alarmListLlEmpty.visibility = View.VISIBLE
            }
        }

        alarmViewModel.deleteAlarmResponse.observe(viewLifecycleOwner) {
            alarmViewModel.getAlarmList()
        }

        alarmViewModel.spinner.observe(viewLifecycleOwner) { show ->
            binding.spinner.visibility = if (show) View.VISIBLE else View.GONE
        }

        alarmViewModel.toast.eventObserve(viewLifecycleOwner) { text ->
            text?.let {
                (requireActivity() as HomeActivity).showToast(it)
            }
        }
    }

    private fun navigateListener(id: Int?, location: String?) {
        when (location) {
            "apply" -> {
                viewModel.currentApplicationTab = ApplicationAdapter.ApplicationType.APPLY

                if (id != null) {
                    val bundle = bundleOf("id" to id)
                    findNavController().navigate(R.id.applicationFragment, bundle)
                } else {
                    findNavController().navigate(R.id.action_alarmListFragment_to_listFragment2)
                }
            }

            "refuse" -> {
                viewModel.currentApplicationTab = ApplicationAdapter.ApplicationType.PARTICIPANT

                if (id != null) {
                    val bundle = bundleOf("id" to id)
                    findNavController().navigate(R.id.applicationFragment, bundle)
                } else {
                    findNavController().navigate(R.id.action_alarmListFragment_to_listFragment2)
                }
            }

            "chat" -> {
                if (id != null) {
                    val bundle = bundleOf("id" to id)
                    findNavController().navigate(R.id.chatFragment, bundle)
                } else {
                    findNavController().navigate(R.id.action_alarmListFragment_to_chatListFragment2)
                }
            }

            else -> {
                findNavController().navigate(R.id.action_alarmListFragment_to_listFragment2)
            }
        }
    }

    private fun setRecyclerView() {
        binding.alarmListRecycler.adapter = adapter
        binding.alarmListRecycler.itemAnimator = null
    }

    override fun onPause() {
        super.onPause()
        if (binding.alarmListRefresh.isRefreshing) binding.alarmListRefresh.isRefreshing = false
    }
}