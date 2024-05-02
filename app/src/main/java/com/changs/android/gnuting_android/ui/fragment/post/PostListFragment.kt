package com.changs.android.gnuting_android.ui.fragment.post

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.data.model.PostResult
import com.changs.android.gnuting_android.databinding.FragmentPostListBinding
import com.changs.android.gnuting_android.ui.HomeActivity
import com.changs.android.gnuting_android.ui.adapter.PostListPagingAdapter
import com.changs.android.gnuting_android.ui.fragment.bottomsheet.SearchPostListBottomSheetFragment
import com.changs.android.gnuting_android.util.PostItemNavigator
import com.changs.android.gnuting_android.util.eventObserve
import com.changs.android.gnuting_android.viewmodel.PostViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@OptIn(ExperimentalCoroutinesApi::class)
@AndroidEntryPoint
class PostListFragment : BaseFragment<FragmentPostListBinding>(
    FragmentPostListBinding::bind, R.layout.fragment_post_list
), PostItemNavigator {
    private val postViewModel: PostViewModel by viewModels()
    private lateinit var adapter: PostListPagingAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecyclerView()
        setObserver()
        setListener()
    }

    private fun setObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            postViewModel.getPostPagingList().collectLatest {
                adapter.submitData(it)
            }
        }

        postViewModel.spinner.observe(viewLifecycleOwner) { show ->
            binding.spinner.visibility = if (show) View.VISIBLE else View.GONE
        }

        postViewModel.toast.eventObserve(viewLifecycleOwner) { text ->
            text?.let {
                (requireActivity() as HomeActivity).showToast(it)
            }
        }

    }

    private fun setListener() {
        binding.postListImgBack.setOnClickListener { findNavController().popBackStack() }

        binding.postListImgPostBtn.setOnClickListener {
            findNavController().navigate(R.id.action_postListFragment_to_postFragment)
        }

        binding.postListRefresh.setColorSchemeColors(resources.getColor(R.color.main, null))
        binding.postListRefresh.setOnRefreshListener {
            viewLifecycleOwner.lifecycleScope.launch {
                postViewModel.getPostPagingList().collectLatest {
                    if (binding.postListRefresh.isRefreshing) binding.postListRefresh.isRefreshing = false
                    adapter.submitData(it)
                }
            }
        }
    }

    private fun setRecyclerView() {
        adapter = PostListPagingAdapter(this)
        adapter.addLoadStateListener {
            if (it.append.endOfPaginationReached) {
                if (adapter.itemCount < 1) {
                    binding.postListLlEmpty.visibility = View.VISIBLE
                } else {
                    binding.postListLlEmpty.visibility = View.GONE
                }
            }

            when (it.refresh) {
                is LoadState.Loading -> binding.spinner.isVisible = true
                is LoadState.NotLoading -> binding.spinner.isVisible = false
                is LoadState.Error ->  (requireActivity() as HomeActivity).showToast("네트워크 에러가 발생했습니다.")
            }
        }
        binding.postListRecyclerview.adapter = adapter
    }

    override fun navigateToDetail(id: Int) {
        val bundle = bundleOf("id" to id)
        findNavController().navigate(R.id.action_global_detailFragment, bundle)
    }

    override fun onPause() {
        super.onPause()
        if (binding.postListRefresh.isRefreshing) binding.postListRefresh.isRefreshing = false
    }
}