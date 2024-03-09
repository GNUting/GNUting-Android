package com.changs.android.gnuting_android.ui.fragment

import android.os.Bundle
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
import com.changs.android.gnuting_android.databinding.FragmentHomeBinding
import com.changs.android.gnuting_android.ui.adapter.HomeAdapter
import com.changs.android.gnuting_android.ui.adapter.ViewPagerAdapter
import com.changs.android.gnuting_android.util.PostItemNavigator
import com.changs.android.gnuting_android.viewmodel.HomeMainViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class HomeFragment :
    BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::bind, R.layout.fragment_home),
    PostItemNavigator {
    private val viewModel: HomeMainViewModel by activityViewModels()
    private lateinit var adapter: HomeAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getPostList()

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
        adapter = HomeAdapter(this)
        binding.homeRecyclerview.adapter = adapter
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

    private fun setObserver() {
        viewModel.myInfo.observe(viewLifecycleOwner) {
            it?.let {
                binding.homeTxtGreetings.text = "${it.nickname}님 안녕하세요!"
                Glide.with(binding.root).load(it.profileImage).error(R.drawable.ic_profile)
                    .into(binding.homeImgProfile)
            }

        }

        viewModel.postResponse.observe(viewLifecycleOwner) {
            adapter.submitList(it.result)
        }
    }

    override fun navigateToDetail(id: Int) {
        val action = DetailFragmentDirections.actionGlobalDetailFragment(id)
        findNavController().navigate(action)
    }
}