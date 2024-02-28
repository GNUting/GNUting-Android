package com.changs.android.gnuting_android.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.data.model.Member
import com.changs.android.gnuting_android.databinding.FragmentJoin1Binding
import com.changs.android.gnuting_android.databinding.FragmentMyBinding

class MyFragment : BaseFragment<FragmentMyBinding>(FragmentMyBinding::bind, R.layout.fragment_my) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.myTxtName.text = "이창형"
        binding.myTxtInfo.text = "컴퓨터공학과 | 26살 | 21학번"
        binding.myTxtIntro.text = "네카라쿠배당토직야몰두센 가고싶다~"

        binding.myTxtMenuMyPosts.setOnClickListener {
            findNavController().navigate(R.id.action_myFragment_to_myPostListFragment)
        }

        binding.myTxtEditProfile.setOnClickListener {
            val member = Member(null, "이창형", "창스", "21학번", "26살", "ISTJ", "네카라쿠배당토직야몰두센 가고싶다~", "sdf123", "컴퓨터공학과")
            val action = MyFragmentDirections.actionMyFragmentToEditProfileFragment(member)
            findNavController().navigate(action)
        }
    }
}