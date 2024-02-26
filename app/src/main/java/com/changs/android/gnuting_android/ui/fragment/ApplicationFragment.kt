package com.changs.android.gnuting_android.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.data.model.Member
import com.changs.android.gnuting_android.databinding.FragmentApplicationBinding
import com.changs.android.gnuting_android.databinding.FragmentListBinding
import com.changs.android.gnuting_android.ui.adapter.ApplicationMemberAdapter


class ApplicationFragment : BaseFragment<FragmentApplicationBinding>(FragmentApplicationBinding::bind, R.layout.fragment_application) {
    private val args: ApplicationFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        args.applicationItem?.let {
            binding.applicationTxtDepartment.text = it.department
            binding.applicationTxtMemberCount.text = "${it.members.size}명"

            if (it.status == 0) {
                binding.applicationTxtStatus.text = "대기중"
                binding.applicationTxtStatus.setBackgroundResource(R.drawable.background_radius_10dp_solid_main)
            } else {
                binding.applicationTxtStatus.text = "성공"
                binding.applicationTxtStatus.setBackgroundResource(R.drawable.background_radius_10dp_solid_secondary)
            }

            val adapter1 = ApplicationMemberAdapter()
            val adapter2 = ApplicationMemberAdapter()

            binding.applicationTxtRecyclerviewHeader1.text = it.department
            binding.applicationTxtRecyclerviewHeader2.text = "미술교육과"

            binding.applicationRecyclerview1.adapter = adapter1
            binding.applicationRecyclerview2.adapter = adapter2

            adapter1.submitList(it.members)
            val members = listOf<Member>(Member(profile = null, "장원영", "워녕", "21학번", "20살", "ISTJ", "안녕하세요 인사올립니다.", "dfsdf", "미술교육과"))
            adapter2.submitList(members)
        }

        binding.applicationImgBack.setOnClickListener {
            findNavController().popBackStack()
        }


    }


}