package com.changs.android.gnuting_android.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.data.model.ApplicationItem
import com.changs.android.gnuting_android.data.model.Member
import com.changs.android.gnuting_android.databinding.FragmentListBinding
import com.changs.android.gnuting_android.databinding.FragmentMyBinding
import com.changs.android.gnuting_android.ui.adapter.ApplicationAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener

class ListFragment : BaseFragment<FragmentListBinding>(FragmentListBinding::bind, R.layout.fragment_list) {
    val adapter by lazy { ApplicationAdapter() }
    val members = listOf(Member(null, "전재욱", "짱짱맨", "19학번", "25살", "ENTP", "안녕하세요 저는 컴퓨터과학과이고 컴퓨터과학을 공부하고 있습니다.", "asd123", "컴퓨터과학과"))
    val applicationList1 = listOf(ApplicationItem("산업시스템공학", members, 1), ApplicationItem("컴퓨터과학과", members, 0), ApplicationItem("간호학과", members, 1))
    val applicationList2 = listOf(ApplicationItem("컴퓨터공학", members, 0), ApplicationItem("의류학과", members, 1))
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecyclerView()

        binding.listTl.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    when(it.position) {
                        0 -> {
                            adapter.submitList(applicationList1)
                        }

                        else -> {
                            adapter.submitList(applicationList2)
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
        adapter.submitList(applicationList1)
    }
}