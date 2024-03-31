package com.changs.android.gnuting_android.ui.fragment.application

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.data.model.ApplicationResult
import com.changs.android.gnuting_android.databinding.FragmentListBinding
import com.changs.android.gnuting_android.ui.adapter.ApplicationAdapter
import com.changs.android.gnuting_android.viewmodel.HomeMainViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
@AndroidEntryPoint
class ListFragment : BaseFragment<FragmentListBinding>(FragmentListBinding::bind, R.layout.fragment_list) {
    private val viewModel: HomeMainViewModel by activityViewModels()
    val applyStateAdapter by lazy { ApplicationAdapter(::applicationItemListener) }
    val receiveStateAdapter by lazy { ApplicationAdapter(::applicationItemListener) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getApplicationReceiveList()
        viewModel.getApplicationApplyList()
        setRecyclerView()
        setObserver()

        binding.listTl.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    when(it.position) {
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
        viewModel.applicationApplyStateResponse.observe(viewLifecycleOwner) {
            applyStateAdapter.submitList(it.result)
        }

        viewModel.applicationReceiveStateResponse.observe(viewLifecycleOwner) {
            receiveStateAdapter.submitList(it.result)

        }
    }

    private fun applicationItemListener(item: ApplicationResult) {
        val action = ListFragmentDirections.actionListFragmentToApplicationFragment(item)
        findNavController().navigate(action)
    }
}