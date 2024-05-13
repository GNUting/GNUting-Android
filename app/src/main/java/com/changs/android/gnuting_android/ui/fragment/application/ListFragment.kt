package com.changs.android.gnuting_android.ui.fragment.application

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.data.model.ApplicationResult
import com.changs.android.gnuting_android.databinding.FragmentListBinding
import com.changs.android.gnuting_android.ui.HomeActivity
import com.changs.android.gnuting_android.ui.adapter.ApplicationAdapter
import com.changs.android.gnuting_android.util.eventObserve
import com.changs.android.gnuting_android.util.showTwoButtonDialog
import com.changs.android.gnuting_android.viewmodel.ApplicationViewModel
import com.changs.android.gnuting_android.viewmodel.HomeMainViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
@AndroidEntryPoint
class ListFragment :
    BaseFragment<FragmentListBinding>(FragmentListBinding::bind, R.layout.fragment_list) {
    private val applicationViewModel: ApplicationViewModel by viewModels()
    private val viewModel: HomeMainViewModel by activityViewModels()
    private val applyStateAdapter by lazy {
        ApplicationAdapter(
            ApplicationAdapter.ApplicationType.APPLY, ::applicationItemListener
        ) {
            showTwoButtonDialog(requireContext(), "신청현황을 삭제하시겠습니까?", rightButtonText = "삭제") {
                applicationViewModel.deleteApplyState(it)
            }
        }
    }
    private val receiveStateAdapter by lazy {
        ApplicationAdapter(
            ApplicationAdapter.ApplicationType.PARTICIPANT, ::applicationItemListener
        ) {
            showTwoButtonDialog(requireContext(), "신청현황을 삭제하시겠습니까?", rightButtonText = "삭제") {
                applicationViewModel.deleteReceivedState(it)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecyclerView()
        setObserver()

        binding.listTl.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    when (it.position) {
                        0 -> {
                            applicationViewModel.getApplicationApplyList()

                            viewModel.currentApplicationTab =
                                ApplicationAdapter.ApplicationType.APPLY
                            binding.listRecyclerview.adapter = applyStateAdapter
                        }

                        else -> {
                            applicationViewModel.getApplicationReceiveList()

                            viewModel.currentApplicationTab =
                                ApplicationAdapter.ApplicationType.PARTICIPANT
                            binding.listRecyclerview.adapter = receiveStateAdapter
                        }
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        when (viewModel.currentApplicationTab) {
            ApplicationAdapter.ApplicationType.APPLY -> {
                binding.listTl.getTabAt(0)?.select()
                applicationViewModel.getApplicationApplyList()
            }

            else -> {
                binding.listTl.getTabAt(1)?.select()
                applicationViewModel.getApplicationReceiveList()
            }
        }

        binding.listRefresh.setColorSchemeColors(resources.getColor(R.color.main, null))
        binding.listRefresh.setOnRefreshListener {
            when (viewModel.currentApplicationTab) {
                ApplicationAdapter.ApplicationType.APPLY -> {
                    binding.listTl.getTabAt(0)?.select()
                    applicationViewModel.getApplicationApplyList()
                }

                else -> {
                    binding.listTl.getTabAt(1)?.select()
                    applicationViewModel.getApplicationReceiveList()
                }
            }
        }
    }

    private fun setRecyclerView() {
        binding.listRecyclerview.adapter = applyStateAdapter
        binding.listRecyclerview.itemAnimator = null
    }

    private fun setObserver() {
        applicationViewModel.deleteApplyStateResponse.eventObserve(viewLifecycleOwner) {
            when (viewModel.currentApplicationTab) {
                ApplicationAdapter.ApplicationType.APPLY -> {
                    binding.listTl.getTabAt(0)?.select()
                    applicationViewModel.getApplicationApplyList()
                }

                else -> {
                    binding.listTl.getTabAt(1)?.select()
                    applicationViewModel.getApplicationReceiveList()
                }
            }
        }

        applicationViewModel.deleteReceivedStateResponse.eventObserve(viewLifecycleOwner) {
            when (viewModel.currentApplicationTab) {
                ApplicationAdapter.ApplicationType.APPLY -> {
                    binding.listTl.getTabAt(0)?.select()
                    applicationViewModel.getApplicationApplyList()
                }

                else -> {
                    binding.listTl.getTabAt(1)?.select()
                    applicationViewModel.getApplicationReceiveList()
                }
            }
        }

        applicationViewModel.spinner.observe(viewLifecycleOwner) { show ->
            binding.spinner.visibility = if (show) View.VISIBLE else View.GONE
        }

        applicationViewModel.toast.eventObserve(viewLifecycleOwner) { text ->
            text?.let {
                (requireActivity() as HomeActivity).showToast(it)
            }
        }

        applicationViewModel.applicationApplyStateResponse.observe(viewLifecycleOwner) {
            if (binding.listRefresh.isRefreshing) binding.listRefresh.isRefreshing = false
            applyStateAdapter.submitList(it.result)

            if (it.result.isNotEmpty()) {
                binding.listLlEmpty.visibility = View.GONE
            } else {
                binding.listLlEmpty.visibility = View.VISIBLE
            }


        }

        applicationViewModel.applicationReceiveStateResponse.observe(viewLifecycleOwner) {
            if (binding.listRefresh.isRefreshing) binding.listRefresh.isRefreshing = false
            receiveStateAdapter.submitList(it.result)

            if (it.result.isNotEmpty()) {
                binding.listLlEmpty.visibility = View.GONE
            } else {
                binding.listLlEmpty.visibility = View.VISIBLE
            }

        }
    }

    private fun applicationItemListener(item: ApplicationResult) {
        val action = ListFragmentDirections.actionListFragmentToApplicationFragment(item.id)
        findNavController().navigate(action)
    }

    override fun onPause() {
        super.onPause()
        if (binding.listRefresh.isRefreshing) binding.listRefresh.isRefreshing = false
    }
}