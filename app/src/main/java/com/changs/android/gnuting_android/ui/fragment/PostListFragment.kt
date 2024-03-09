package com.changs.android.gnuting_android.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.databinding.FragmentPostListBinding
import com.changs.android.gnuting_android.ui.adapter.PostListPagingAdapter
import com.changs.android.gnuting_android.util.PostItemNavigator
import com.changs.android.gnuting_android.viewmodel.HomeMainViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@OptIn(ExperimentalCoroutinesApi::class)
class PostListFragment : BaseFragment<FragmentPostListBinding>(FragmentPostListBinding::bind, R.layout.fragment_post_list), PostItemNavigator {
    private val viewModel: HomeMainViewModel by activityViewModels()
    private lateinit var adapter: PostListPagingAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecyclerView()
        setListener()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getPostPagingList().collectLatest {
                adapter.submitData(it)
            }
        }
    }

    private fun setListener() {
        binding.postListImgBack.setOnClickListener { findNavController().popBackStack() }

        binding.postListCardPost.setOnClickListener {
            findNavController().navigate(R.id.action_postListFragment_to_postFragment)
        }

        binding.postListImgSearch.setOnClickListener {
            findNavController().navigate(R.id.action_postListFragment_to_searchPostListFragment)
        }
    }

    private fun setRecyclerView() {
        adapter = PostListPagingAdapter(this)
        binding.postListRecyclerview.adapter = adapter
    }

    override fun navigateToDetail(id: Int) {
        val action = DetailFragmentDirections.actionGlobalDetailFragment(id)
        findNavController().navigate(action)
    }
}