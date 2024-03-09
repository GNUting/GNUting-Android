package com.changs.android.gnuting_android.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.data.model.ApplicationItem
import com.changs.android.gnuting_android.data.model.ApplicationResult
import com.changs.android.gnuting_android.data.model.Member
import com.changs.android.gnuting_android.databinding.FragmentListBinding
import com.changs.android.gnuting_android.databinding.FragmentMyBinding
import com.changs.android.gnuting_android.ui.adapter.ApplicationAdapter
import com.changs.android.gnuting_android.viewmodel.HomeMainViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class ListFragment : BaseFragment<FragmentListBinding>(FragmentListBinding::bind, R.layout.fragment_list) {
    private val viewModel: HomeMainViewModel by activityViewModels()
    val adapter by lazy { ApplicationAdapter(::applicationItemListener) }
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
                            viewModel.applicationApplyStateResponse.value?.let {
                                adapter.submitList(it.result)
                            }
                        }

                        else -> {
                            viewModel.applicationReceiveStateResponse.value?.let {
                                adapter.submitList(it.result)
                            }
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
        binding.listRecyclerview.adapter = adapter
        binding.listRecyclerview.itemAnimator = null
    }

    private fun setObserver() {
        viewModel.applicationApplyStateResponse.observe(viewLifecycleOwner) {
            adapter.submitList(it.result)
        }

        viewModel.applicationReceiveStateResponse.observe(viewLifecycleOwner) {
            adapter.submitList(it.result)

        }
    }

    private fun applicationItemListener(item: ApplicationResult) {
        val action = ListFragmentDirections.actionListFragmentToApplicationFragment(item)
        findNavController().navigate(action)
    }
}