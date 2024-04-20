package com.changs.android.gnuting_android.ui.fragment.home

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
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
import com.changs.android.gnuting_android.ui.HomeActivity
import com.changs.android.gnuting_android.ui.adapter.HomeAdapter
import com.changs.android.gnuting_android.ui.adapter.ViewPagerAdapter
import com.changs.android.gnuting_android.ui.fragment.bottomsheet.SearchPostListBottomSheetFragment
import com.changs.android.gnuting_android.util.PostItemNavigator
import com.changs.android.gnuting_android.util.changeStatusBarColor
import com.changs.android.gnuting_android.util.eventObserve
import com.changs.android.gnuting_android.viewmodel.AlarmViewModel
import com.changs.android.gnuting_android.viewmodel.HomeMainViewModel
import com.changs.android.gnuting_android.viewmodel.PostViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class HomeFragment :
    BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::bind, R.layout.fragment_home) {
    private val viewModel: HomeMainViewModel by activityViewModels()
    private val alarmViewModel: AlarmViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        alarmViewModel.getNewAlarm()
        requireActivity().changeStatusBarColor(R.color.pink)
        setListener()
        setObserver()
        // setPager()
    }

    private fun setListener() {
        binding.homeImgSearch.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_postSearchFragment)
        }

        binding.homeBtnPost.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_post_graph)
        }

        binding.homeCardPostList.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_postListFragment)
        }

        binding.homeCardMyPostList.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_myPostListFragment)
        }

        binding.homeImgAlarm.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_alarmListFragment)
        }

        binding.homeImgProfile.setOnClickListener {
            (requireActivity() as HomeActivity).selectedItemId(R.id.myFragment)
        }

        binding.homeImgBanner.setOnClickListener {
            runCatching {
                val uri = Uri.parse("https://www.instagram.com/gnu_ting/p/C5bIzh2yIe5/?img_index=1")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                intent.setPackage("com.instagram.android")
                startActivity(intent)
            }.onFailure {
                Timber.e(it.message ?: "error")
                val uri = Uri.parse("https://www.instagram.com/gnu_ting/p/C5bIzh2yIe5/?img_index=1")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
        }
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
                binding.homeTxtGreetings.text = "${it.nickname} 님 안녕하세요 :)"
                Glide.with(binding.root).load(it.profileImage).error(R.drawable.ic_profile)
                    .into(binding.homeImgProfile)
            }
        }

        alarmViewModel.newAlarmResponse.observe(viewLifecycleOwner) {
            binding.homeImgNewAlarm.isVisible = it.result
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().changeStatusBarColor(R.color.white)
    }

}