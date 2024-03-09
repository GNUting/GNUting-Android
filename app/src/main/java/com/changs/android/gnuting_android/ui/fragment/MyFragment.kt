package com.changs.android.gnuting_android.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.data.model.Member
import com.changs.android.gnuting_android.databinding.FragmentJoin1Binding
import com.changs.android.gnuting_android.databinding.FragmentMyBinding
import com.changs.android.gnuting_android.viewmodel.HomeMainViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class MyFragment : BaseFragment<FragmentMyBinding>(FragmentMyBinding::bind, R.layout.fragment_my) {
    private val viewModel: HomeMainViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.myInfo.value?.let { myInfo ->
            binding.myTxtName.text = myInfo.nickname
            binding.myTxtInfo.text = "${myInfo.department} | ${myInfo.age} | ${myInfo.studentId}"
            binding.myTxtIntro.text = myInfo.userSelfIntroduction

            binding.myTxtEditProfile.setOnClickListener {
                val action = MyFragmentDirections.actionMyFragmentToEditProfileFragment(myInfo)
                findNavController().navigate(action)
            }
        }

        binding.myTxtMenuReport.setOnClickListener {
            findNavController().navigate(R.id.action_global_reportFragment)
        }

        binding.myTxtMenuMyPosts.setOnClickListener {
            findNavController().navigate(R.id.action_myFragment_to_myPostListFragment)
        }

    }
}