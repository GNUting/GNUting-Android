package com.changs.android.gnuting_android.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.databinding.FragmentMyPostListBinding
import com.changs.android.gnuting_android.ui.adapter.PostListAdapter
import com.changs.android.gnuting_android.ui.adapter.PostListPagingAdapter
import com.changs.android.gnuting_android.util.PostItemNavigator
import com.changs.android.gnuting_android.viewmodel.HomeMainViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@ExperimentalCoroutinesApi
class MyPostListFragment : BaseFragment<FragmentMyPostListBinding>(FragmentMyPostListBinding::bind, R.layout.fragment_my_post_list), PostItemNavigator {
    private val viewModel: HomeMainViewModel by activityViewModels()
    private lateinit var adapter: PostListPagingAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRecyclerView()
        setListener()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getMyPostPagingList().collectLatest {
                adapter.submitData(it)
            }
        }
    }

    private fun setListener() {
        binding.myPostListImgBack.setOnClickListener { findNavController().popBackStack() }
    }

    private fun setRecyclerView() {
        adapter = PostListPagingAdapter(this)
        binding.myPostListRecyclerview.adapter = adapter
    }

    override fun navigateToDetail(id: Int) {
        val action = DetailFragmentDirections.actionGlobalDetailFragment(id)
        findNavController().navigate(action)
    }
}