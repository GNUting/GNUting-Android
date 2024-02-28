package com.changs.android.gnuting_android.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.databinding.FragmentDetailBinding
import com.changs.android.gnuting_android.databinding.FragmentListBinding
import com.changs.android.gnuting_android.ui.adapter.SpinnerAdapter
import com.changs.android.gnuting_android.util.CurrentMemberBottomSheetFragment

class DetailFragment : BaseFragment<FragmentDetailBinding>(FragmentDetailBinding::bind, R.layout.fragment_detail) {
    private val args: DetailFragmentArgs by navArgs()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.detailImgBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.detailTxtCurrentParticipant.setOnClickListener {
            val bottomDialogFragment = CurrentMemberBottomSheetFragment()
            bottomDialogFragment.show(childFragmentManager, bottomDialogFragment.tag)
        }

        args.detail?.let {
            binding.detailTxtNickname.text = it.nickName
            binding.detailTxtInfo.text = "${it.department} | ${it.studentId}"
            binding.detailTxtDetail.text = it.detail
            binding.detailTxtCurrentParticipant.text = "현재 채팅/참여중인 사람 ${it.memberNum}명"
        }

        binding.detailImgSetting.setOnClickListener {
            binding.detailSpinner.performClick()
        }
        binding.detailSpinner.adapter = SpinnerAdapter(
            requireContext(), resources.getStringArray(R.array.post_setting).toList()
        )

        binding.detailSpinner.setSelection(3, false)

        binding.detailSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    when (position) {
                        0 -> {

                            binding.detailSpinner.setSelection(3, false)
                        }
                        1 -> {
                            binding.detailSpinner.setSelection(3, false)
                        }

                        2 -> {
                            findNavController().navigate(R.id.action_global_reportFragment)
                            binding.detailSpinner.setSelection(3, false)
                        }

                        else -> {

                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }

        // Mock Test
        Glide.with(this).load(R.drawable.ic_profile).into(binding.detailImgProfile)
        binding.detailTxtPostTitle.text = "안녕하세요! 2:2 미팅하실 분~"
        binding.detailTxtNickname.text = "킹왕짱"
        binding.detailTxtInfo.text = "컴퓨터과학과 | 23학번"
        binding.detailTxtDetail.text = "안녕하세요!\n" +
                "저희는 컴퓨터과학과 1학년 4명입니다.\n" +
                "저희 모두 23학번이구요\n" +
                "04년생 3명\n" +
                "03년생 1명 입니다.\n" +
                "\n" +
                "저희랑 재밌게 과팅해요!!!\n" +
                "\n" +
                "편하게 채팅 신청해주세요~"
        binding.detailTxtCurrentParticipant.text = "현재 채팅/참여중인 사람 ${4}명"

    }
}