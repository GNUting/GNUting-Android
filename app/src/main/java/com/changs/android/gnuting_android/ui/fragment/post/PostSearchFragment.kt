package com.changs.android.gnuting_android.ui.fragment.post

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.databinding.FragmentPostSearchBinding
import com.changs.android.gnuting_android.ui.HomeActivity
import com.changs.android.gnuting_android.ui.adapter.PostSearchListPagingAdapter
import com.changs.android.gnuting_android.util.PostItemNavigator
import com.changs.android.gnuting_android.util.eventObserve
import com.changs.android.gnuting_android.viewmodel.PostViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@AndroidEntryPoint
class PostSearchFragment : BaseFragment<FragmentPostSearchBinding>(
    FragmentPostSearchBinding::bind, R.layout.fragment_post_search
), PostItemNavigator {
    private val postViewModel: PostViewModel by viewModels()
    private lateinit var adapter: PostSearchListPagingAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecyclerView()
        setListener()
        setObserver()
    }

    private fun setListener() {
        binding.postSearchEditSearch.doOnTextChanged { _, _, _, _ ->
            viewLifecycleOwner.lifecycleScope.launch {
                postViewModel.getSearchPostPagingList(binding.postSearchEditSearch.text.toString())
                    .collectLatest {
                        adapter.submitData(it)
                        binding.postSearchRecyclerview.smoothScrollToPosition(0)
                    }
            }
        }

        binding.postSearchImgBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.postSearchTxtCancel.setOnClickListener {
            binding.postSearchEditSearch.text?.clear()
        }

        binding.postSearchRefresh.setColorSchemeColors(resources.getColor(R.color.main, null))
        binding.postSearchRefresh.setOnRefreshListener {
            viewLifecycleOwner.lifecycleScope.launch {
                postViewModel.getSearchPostPagingList(binding.postSearchEditSearch.text.toString())
                    .collectLatest {
                        if (binding.postSearchRefresh.isRefreshing) binding.postSearchRefresh.isRefreshing = false
                        adapter.submitData(it)
                        binding.postSearchRecyclerview.smoothScrollToPosition(0)
                    }
            }
        }
    }

    private fun setObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            postViewModel.getSearchPostPagingList("").collectLatest {
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

    private fun setRecyclerView() {
        adapter = PostSearchListPagingAdapter(this)
        adapter.addLoadStateListener {
            if (it.append.endOfPaginationReached) {
                if (adapter.itemCount < 1) {
                    binding.postSearchLlEmpty.visibility = View.VISIBLE
                } else {
                    binding.postSearchLlEmpty.visibility = View.GONE
                }
            }

            when (it.refresh) {
                is LoadState.Loading -> binding.spinner.isVisible = true
                is LoadState.NotLoading -> binding.spinner.isVisible = false
                is LoadState.Error -> (requireActivity() as HomeActivity).showToast("네트워크 에러가 발생했습니다.")
            }
        }
        binding.postSearchRecyclerview.adapter = adapter
    }

    override fun navigateToDetail(id: Int) {
        val bundle = bundleOf("id" to id)
        findNavController().navigate(R.id.action_global_detailFragment, bundle)
    }

    override fun onPause() {
        super.onPause()
        if (binding.postSearchRefresh.isRefreshing) binding.postSearchRefresh.isRefreshing = false
    }
}