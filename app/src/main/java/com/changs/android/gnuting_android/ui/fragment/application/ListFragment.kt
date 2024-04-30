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
        )
    }
    private val receiveStateAdapter by lazy {
        ApplicationAdapter(
            ApplicationAdapter.ApplicationType.PARTICIPANT, ::applicationItemListener
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applicationViewModel.getApplicationReceiveList()
        applicationViewModel.getApplicationApplyList()
        setRecyclerView()
        setObserver()

        binding.listTl.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    when (it.position) {
                        0 -> {
                            viewModel.currentApplicationTab = ApplicationAdapter.ApplicationType.APPLY
                            binding.listRecyclerview.adapter = applyStateAdapter
                        }

                        else -> {
                            viewModel.currentApplicationTab = ApplicationAdapter.ApplicationType.PARTICIPANT
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
            }
            else -> {
                binding.listTl.getTabAt(1)?.select()
            }
        }
    }

    private fun setRecyclerView() {
        binding.listRecyclerview.adapter = applyStateAdapter
        binding.listRecyclerview.itemAnimator = null
    }

    private fun setObserver() {
        applicationViewModel.spinner.observe(viewLifecycleOwner) { show ->
            binding.spinner.visibility = if (show) View.VISIBLE else View.GONE
        }

        applicationViewModel.toast.eventObserve(viewLifecycleOwner) { text ->
            text?.let {
                (requireActivity() as HomeActivity).showToast(it)
            }
        }

        applicationViewModel.applicationApplyStateResponse.observe(viewLifecycleOwner) {
            applyStateAdapter.submitList(it.result)

            if (it.result.isNotEmpty()) {
                binding.listLlEmpty.visibility = View.GONE
            }
            else {
                binding.listLlEmpty.visibility = View.VISIBLE
            }
        }

        applicationViewModel.applicationReceiveStateResponse.observe(viewLifecycleOwner) {
            receiveStateAdapter.submitList(it.result)

            if (it.result.isNotEmpty()) {
                binding.listLlEmpty.visibility = View.GONE
            }
            else {
                binding.listLlEmpty.visibility = View.VISIBLE
            }

        }
    }

    private fun applicationItemListener(item: ApplicationResult) {
        val action = ListFragmentDirections.actionListFragmentToApplicationFragment(item)
        findNavController().navigate(action)
    }
}