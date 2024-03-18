package com.changs.android.gnuting_android.ui.fragment

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.databinding.FragmentPostListBinding
import com.changs.android.gnuting_android.databinding.FragmentSearchPostListBinding
import com.changs.android.gnuting_android.ui.adapter.PostListAdapter
import com.changs.android.gnuting_android.ui.adapter.PostSearchListAdapter
import com.changs.android.gnuting_android.ui.adapter.PostSearchListPagingAdapter
import com.changs.android.gnuting_android.util.PostItemNavigator
import com.changs.android.gnuting_android.util.hideSoftKeyboard
import com.changs.android.gnuting_android.viewmodel.HomeMainViewModel
import com.changs.android.gnuting_android.viewmodel.SearchViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@OptIn(ExperimentalCoroutinesApi::class)
class SearchPostListFragment : BaseFragment<FragmentSearchPostListBinding>(FragmentSearchPostListBinding::bind, R.layout.fragment_search_post_list), PostItemNavigator {
    private val viewModel: HomeMainViewModel by activityViewModels()
    private lateinit var adapter: PostSearchListPagingAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecyclerView()
        setListener()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getSearchPostPagingList("").collectLatest {
                adapter.submitData(it)
            }
        }
    }

    private fun setListener() {
        binding.searchPostListEditSearch.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewModel.getSearchPostPagingList(binding.searchPostListEditSearch.text.toString())
                            .collectLatest {
                                adapter.submitData(it)
                            }
                    }
                    return true
                }
                return false
            }
        })

        binding.searchPostListTxtCancel.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setRecyclerView() {
        adapter = PostSearchListPagingAdapter(this)
        binding.searchPostListRecyclerview.adapter = adapter
    }

    override fun navigateToDetail(id: Int) {
        val action = DetailFragmentDirections.actionGlobalDetailFragment(id)
        findNavController().navigate(action)
    }
}