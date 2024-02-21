package com.changs.android.gnuting_android.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.data.model.DetailPostItem
import com.changs.android.gnuting_android.data.model.PostListItem
import com.changs.android.gnuting_android.databinding.FragmentListBinding
import com.changs.android.gnuting_android.databinding.FragmentPostListBinding
import com.changs.android.gnuting_android.ui.adapter.PostListAdapter
import com.changs.android.gnuting_android.util.PostItemNavigator
import com.changs.android.gnuting_android.util.hideSoftKeyboard
import com.changs.android.gnuting_android.viewmodel.PostListViewModel


class PostListFragment : BaseFragment<FragmentPostListBinding>(FragmentPostListBinding::bind, R.layout.fragment_post_list), PostItemNavigator {
    private val viewModel: PostListViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecyclerView()

        binding.postListImgBack.setOnClickListener { findNavController().popBackStack() }

        binding.postListCardPost.setOnClickListener {
            findNavController().navigate(R.id.action_postListFragment_to_postFragment)
        }

        binding.postListImgSearch.setOnClickListener {
            binding.postListClDefault.visibility = View.INVISIBLE
            binding.postListLlSearch.visibility = View.VISIBLE
        }

        binding.postListTxtCancel.setOnClickListener {
            binding.postListLlSearch.visibility = View.INVISIBLE
            binding.postListEditSearch.hideSoftKeyboard()
            binding.postListEditSearch.text?.clear()
            binding.postListClDefault.visibility = View.VISIBLE
        }
    }

    private fun setRecyclerView() {
        val adapter = PostListAdapter(this)
        binding.postListRecyclerview.adapter = adapter
        adapter.submitList(viewModel.postItems)
    }

    override fun navigateToDetail(itemPosition: Int) {
        val action = DetailFragmentDirections.actionGlobalDetailFragment()
        findNavController().navigate(action)
    }
}