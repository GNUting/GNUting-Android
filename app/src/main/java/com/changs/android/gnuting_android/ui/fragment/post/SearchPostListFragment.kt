package com.changs.android.gnuting_android.ui.fragment.post

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.changs.android.gnuting_android.GNUApplication
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.databinding.FragmentSearchPostListBinding
import com.changs.android.gnuting_android.ui.MainActivity
import com.changs.android.gnuting_android.ui.adapter.PostSearchListPagingAdapter
import com.changs.android.gnuting_android.util.PostItemNavigator
import com.changs.android.gnuting_android.util.eventObserve
import com.changs.android.gnuting_android.viewmodel.HomeMainViewModel
import com.changs.android.gnuting_android.viewmodel.PostViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SearchPostListFragment : BaseFragment<FragmentSearchPostListBinding>(
    FragmentSearchPostListBinding::bind, R.layout.fragment_search_post_list
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
        binding.searchPostListEditSearch.doOnTextChanged { _, _, _, _ ->
            viewLifecycleOwner.lifecycleScope.launch {
                postViewModel.getSearchPostPagingList(binding.searchPostListEditSearch.text.toString())
                    .collectLatest {
                        adapter.submitData(it)
                    }
            }
        }

        binding.searchPostListImgBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.searchPostListTxtCancel.setOnClickListener {
            binding.searchPostListEditSearch.text?.clear()
        }
    }

    private fun setObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            postViewModel.getSearchPostPagingList("").collectLatest {
                adapter.submitData(it)
            }
        }

        postViewModel.expirationToken.eventObserve(viewLifecycleOwner) {
            GNUApplication.sharedPreferences.edit().clear().apply()
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        postViewModel.spinner.observe(viewLifecycleOwner) { show ->
            binding.spinner.visibility = if (show) View.VISIBLE else View.GONE
        }

        postViewModel.toast.eventObserve(viewLifecycleOwner) { text ->
            text?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun setRecyclerView() {
        adapter = PostSearchListPagingAdapter(this)
        binding.searchPostListRecyclerview.adapter = adapter
    }

    override fun navigateToDetail(id: Int) {
        val bundle = bundleOf("id" to id)
        findNavController().navigate(R.id.action_global_detailFragment, bundle)
    }
}