package com.changs.android.gnuting_android.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.data.model.DetailPostItem
import com.changs.android.gnuting_android.data.model.PostListItem
import com.changs.android.gnuting_android.databinding.FragmentHomeBinding
import com.changs.android.gnuting_android.ui.adapter.HomeAdapter
import com.changs.android.gnuting_android.ui.adapter.ViewPagerAdapter
import com.changs.android.gnuting_android.util.PostItemNavigator
import com.changs.android.gnuting_android.viewmodel.HomeMainViewModel
import com.changs.android.gnuting_android.viewmodel.HomeViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class HomeFragment :
    BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::bind, R.layout.fragment_home),
    PostItemNavigator {
    private val homeViewModel: HomeViewModel by viewModels()
    private val viewModel: HomeMainViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListener()
        setPager()
        setRecyclerView()
        setObserver()
    }

    private fun setListener() {
        binding.homeTxtMoreList.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_postListFragment)
        }
    }

    private fun setRecyclerView() {
        val adapter = HomeAdapter(this)
        binding.homeRecyclerview.adapter = adapter
        adapter.submitList(homeViewModel.posts)

    }

    private fun setPager() {
        binding.homePager.adapter = ViewPagerAdapter(homeViewModel.images)
        binding.dotsIndicator.attachTo(binding.homePager)


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                while (isActive) {
                    if (binding.homePager.currentItem == homeViewModel.images.size - 1) {
                        homeViewModel.currentItem = 0
                    }
                    binding.homePager.currentItem = homeViewModel.currentItem++
                    delay(5000)
                }
            }
        }
    }

    private fun setObserver() {
        viewModel.myInfo.observe(viewLifecycleOwner) {
                binding.homeTxtGreetings.text = "${it.nickname}님 안녕하세요!"
                Glide.with(binding.root).load(it.profileImage).error(R.drawable.ic_profile)
                    .into(binding.homeImgProfile)
        }
    }

    override fun navigateToDetail(itemPosition: Int) {
        val action = DetailFragmentDirections.actionGlobalDetailFragment()
        findNavController().navigate(action)
    }
}