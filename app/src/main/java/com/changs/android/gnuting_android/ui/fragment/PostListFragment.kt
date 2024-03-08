package com.changs.android.gnuting_android.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.databinding.FragmentPostListBinding
import com.changs.android.gnuting_android.ui.adapter.PostListAdapter
import com.changs.android.gnuting_android.util.PostItemNavigator
import com.changs.android.gnuting_android.util.hideSoftKeyboard
import com.changs.android.gnuting_android.viewmodel.HomeMainViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi


@OptIn(ExperimentalCoroutinesApi::class)
class PostListFragment : BaseFragment<FragmentPostListBinding>(FragmentPostListBinding::bind, R.layout.fragment_post_list), PostItemNavigator {
    private val viewModel: HomeMainViewModel by activityViewModels()
    private lateinit var adapter: PostListAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getPostList()

        setRecyclerView()
        setListener()
        setObserver()
    }

    private fun setListener() {
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
        adapter = PostListAdapter(this)
        binding.postListRecyclerview.adapter = adapter
    }

    private fun setObserver() {
        viewModel.postResponse.observe(viewLifecycleOwner) {
            adapter.submitList(it.result)
        }
    }

    override fun navigateToDetail(id: Int) {
        val action = DetailFragmentDirections.actionGlobalDetailFragment(id)
        findNavController().navigate(action)
    }
}