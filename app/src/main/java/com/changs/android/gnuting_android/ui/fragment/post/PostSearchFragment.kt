package com.changs.android.gnuting_android.ui.fragment.post

import android.app.Dialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.data.model.InUser
import com.changs.android.gnuting_android.databinding.FragmentPostSearchBinding
import com.changs.android.gnuting_android.databinding.SearchMemberBottomSheetBinding
import com.changs.android.gnuting_android.ui.HomeActivity
import com.changs.android.gnuting_android.ui.adapter.AddMemberAdapter
import com.changs.android.gnuting_android.ui.adapter.PostSearchListPagingAdapter
import com.changs.android.gnuting_android.ui.adapter.SelectedMemberAdapter
import com.changs.android.gnuting_android.util.PostItemNavigator
import com.changs.android.gnuting_android.util.eventObserve
import com.changs.android.gnuting_android.viewmodel.HomeMainViewModel
import com.changs.android.gnuting_android.viewmodel.MemberAddViewModel
import com.changs.android.gnuting_android.viewmodel.PostViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
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
                    }
            }
        }

        binding.postSearchImgBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.postSearchTxtCancel.setOnClickListener {
            binding.postSearchEditSearch.text?.clear()
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
}