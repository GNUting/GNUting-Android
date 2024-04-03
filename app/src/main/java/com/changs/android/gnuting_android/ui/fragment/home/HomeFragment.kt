package com.changs.android.gnuting_android.ui.fragment.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.changs.android.gnuting_android.GNUApplication
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.databinding.FragmentHomeBinding
import com.changs.android.gnuting_android.ui.HomeActivity
import com.changs.android.gnuting_android.ui.MainActivity
import com.changs.android.gnuting_android.ui.adapter.HomeAdapter
import com.changs.android.gnuting_android.ui.adapter.ViewPagerAdapter
import com.changs.android.gnuting_android.util.PostItemNavigator
import com.changs.android.gnuting_android.util.eventObserve
import com.changs.android.gnuting_android.viewmodel.HomeMainViewModel
import com.changs.android.gnuting_android.viewmodel.PostViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class HomeFragment :
    BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::bind, R.layout.fragment_home),
    PostItemNavigator {
    private val viewModel: HomeMainViewModel by activityViewModels()
    private val postViewModel: PostViewModel by viewModels()
    private lateinit var adapter: HomeAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postViewModel.getPostList()
        setListener()
        setPager()
        setRecyclerView()
        setObserver()
    }

    private fun setListener() {
        binding.homeTxtMoreList.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_postListFragment)
        }

        binding.homeImgAlarm.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_alarmListFragment)
        }

        binding.homeImgProfile.setOnClickListener {
            (requireActivity() as HomeActivity).selectedItemId(R.id.myFragment)
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


        viewModel.myInfo.observe(viewLifecycleOwner) {
            it?.let {
                binding.homeTxtGreetings.text = "${it.nickname}님 안녕하세요!"
                Glide.with(binding.root).load(it.profileImage).error(R.drawable.ic_profile)
                    .into(binding.homeImgProfile)
            }

        }

        postViewModel.postResponse.observe(viewLifecycleOwner) {
            adapter.submitList(it.result)
        }
    }

    override fun navigateToDetail(id: Int) {
        val bundle = bundleOf("id" to id)
        findNavController().navigate(R.id.action_global_detailFragment, bundle)
    }
}