package com.changs.android.gnuting_android.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.databinding.FragmentPostListBinding
import com.changs.android.gnuting_android.databinding.FragmentSearchPostListBinding
import com.changs.android.gnuting_android.ui.adapter.PostListAdapter
import com.changs.android.gnuting_android.ui.adapter.PostSearchListAdapter
import com.changs.android.gnuting_android.util.PostItemNavigator
import com.changs.android.gnuting_android.util.hideSoftKeyboard
import com.changs.android.gnuting_android.viewmodel.HomeMainViewModel
import com.changs.android.gnuting_android.viewmodel.SearchViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.ExperimentalCoroutinesApi


@OptIn(ExperimentalCoroutinesApi::class)
class SearchPostListFragment : BaseFragment<FragmentSearchPostListBinding>(FragmentSearchPostListBinding::bind, R.layout.fragment_search_post_list), PostItemNavigator {
    private val viewModel: HomeMainViewModel by activityViewModels()
    private val searchViewModel: SearchViewModel by viewModels { SearchViewModel.Factory }
    private lateinit var adapter: PostSearchListAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchViewModel.getSearchPost("")

        searchViewModel.snackbar.observe(viewLifecycleOwner) { text ->
            text?.let {
                Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()
                viewModel.onSnackbarShown()
            }
        }

        setRecyclerView()
        setListener()
        setObserver()
    }

    private fun setListener() {
        binding.searchPostListEditSearch.addTextChangedListener {
            searchViewModel.getSearchPost(it?.toString() ?: "")
        }

        binding.searchPostListTxtCancel.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setRecyclerView() {
        adapter = PostSearchListAdapter(this)
        binding.searchPostListRecyclerview.adapter = adapter
    }

    private fun setObserver() {
        searchViewModel.searchPostResponse.observe(viewLifecycleOwner) {
            adapter.submitList(it.result.content)
        }
    }

    override fun navigateToDetail(id: Int) {
        val action = DetailFragmentDirections.actionGlobalDetailFragment(id)
        findNavController().navigate(action)
    }
}