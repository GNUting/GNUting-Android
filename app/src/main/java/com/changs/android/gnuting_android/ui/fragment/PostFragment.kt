package com.changs.android.gnuting_android.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.databinding.FragmentPostBinding
import com.changs.android.gnuting_android.ui.adapter.PostMemberAdapter
import com.changs.android.gnuting_android.util.AddMemberDialog
import com.changs.android.gnuting_android.viewmodel.HomeMainViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class PostFragment : BaseFragment<FragmentPostBinding>(FragmentPostBinding::bind, R.layout.fragment_post) {
    private val viewModel: HomeMainViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecyclerView()
        binding.postTxtMemberTitle.text = "멤버 (${viewModel.members.size})"
        binding.postImgBack.setOnClickListener { findNavController().popBackStack() }

        binding.postLlAddMember.setOnClickListener {
            val dialog = AddMemberDialog()
            dialog.show(childFragmentManager, null)
        }
    }

    private fun setRecyclerView() {
        val adapter = PostMemberAdapter()
        binding.postRecyclerview.adapter = adapter
        adapter.submitList(viewModel.members)
    }
}