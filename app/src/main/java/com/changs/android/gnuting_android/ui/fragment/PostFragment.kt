package com.changs.android.gnuting_android.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.databinding.FragmentMyBinding
import com.changs.android.gnuting_android.databinding.FragmentPostBinding
import com.changs.android.gnuting_android.ui.adapter.PostMemberAdapter
import com.changs.android.gnuting_android.viewmodel.PostViewModel

class PostFragment : BaseFragment<FragmentPostBinding>(FragmentPostBinding::bind, R.layout.fragment_post) {
    private val viewModel: PostViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecyclerView()
        binding.postTxtMemberTitle.text = "멤버 (${viewModel.members.size})"
        binding.postImgBack.setOnClickListener { findNavController().popBackStack() }
    }

    private fun setRecyclerView() {
        val adapter = PostMemberAdapter()
        binding.postRecyclerview.adapter = adapter
        adapter.submitList(viewModel.members)
    }
}