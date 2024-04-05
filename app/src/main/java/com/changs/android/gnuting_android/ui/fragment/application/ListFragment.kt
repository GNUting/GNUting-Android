package com.changs.android.gnuting_android.ui.fragment.application

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.changs.android.gnuting_android.GNUApplication
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.data.model.ApplicationResult
import com.changs.android.gnuting_android.databinding.FragmentListBinding
import com.changs.android.gnuting_android.ui.HomeActivity
import com.changs.android.gnuting_android.ui.MainActivity
import com.changs.android.gnuting_android.ui.adapter.ApplicationAdapter
import com.changs.android.gnuting_android.util.eventObserve
import com.changs.android.gnuting_android.viewmodel.ApplicationViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
@AndroidEntryPoint
class ListFragment :
    BaseFragment<FragmentListBinding>(FragmentListBinding::bind, R.layout.fragment_list) {
    private val applicationViewModel: ApplicationViewModel by viewModels()
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
                            binding.listRecyclerview.adapter = applyStateAdapter
                        }

                        else -> {
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
        }

        applicationViewModel.applicationReceiveStateResponse.observe(viewLifecycleOwner) {
            receiveStateAdapter.submitList(it.result)

        }
    }

    private fun applicationItemListener(item: ApplicationResult) {
        val action = ListFragmentDirections.actionListFragmentToApplicationFragment(item)
        findNavController().navigate(action)
    }
}