package com.changs.android.gnuting_android.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.data.model.DetailPostItem
import com.changs.android.gnuting_android.data.model.PostListItem
import com.changs.android.gnuting_android.databinding.FragmentHomeBinding
import com.changs.android.gnuting_android.ui.adapter.HomeAdapter
import com.changs.android.gnuting_android.ui.adapter.ViewPagerAdapter
import com.changs.android.gnuting_android.util.PostItemNavigator
import com.changs.android.gnuting_android.viewmodel.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class HomeFragment :
    BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::bind, R.layout.fragment_home), PostItemNavigator {
    private val viewModel: HomeViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.homeTxtGreetings.text = "이창형님 안녕하세요!"
        binding.homeTxtMoreList.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_postListFragment)
        }

        setPager()
        setRecyclerView()
    }

    private fun setRecyclerView() {
        val adapter = HomeAdapter(this)
        binding.homeRecyclerview.adapter = adapter
        adapter.submitList(viewModel.posts)

    }

    private fun setPager() {
        binding.homePager.adapter = ViewPagerAdapter(viewModel.images)
        binding.dotsIndicator.attachTo(binding.homePager)


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                while (isActive) {
                    if (binding.homePager.currentItem == viewModel.images.size - 1) {
                        viewModel.currentItem = 0
                    }
                    binding.homePager.currentItem = viewModel.currentItem++
                    delay(5000)
                }
            }
        }
    }

    override fun navigateToDetail(itemPosition: Int) {
        val action = DetailFragmentDirections.actionGlobalDetailFragment()
        findNavController().navigate(action)
    }
}