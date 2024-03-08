package com.changs.android.gnuting_android.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.databinding.FragmentDetailBinding
import com.changs.android.gnuting_android.ui.adapter.SpinnerAdapter
import com.changs.android.gnuting_android.viewmodel.HomeMainViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class DetailFragment :
    BaseFragment<FragmentDetailBinding>(FragmentDetailBinding::bind, R.layout.fragment_detail) {
    private val viewModel: HomeMainViewModel by activityViewModels()
    private val args: DetailFragmentArgs by navArgs()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getPostDetail(args.id)
        setListener()
        setObserver()
        setSpinner()
    }

    private fun setListener() {
        binding.detailImgBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.detailTxtCurrentParticipant.setOnClickListener {
            val bottomDialogFragment = CurrentMemberBottomSheetFragment()
            bottomDialogFragment.show(childFragmentManager, bottomDialogFragment.tag)
        }

    }

    private fun setSpinner() {
        // TODO: 뷰모델로 이전
        var isSpinnerEventPossible = false

        binding.detailImgSetting.setOnClickListener {
            binding.detailSpinner.performClick()
            isSpinnerEventPossible = true
        }

        val adapter = SpinnerAdapter(
            requireContext(), resources.getStringArray(R.array.post_setting).toList()
        )
        binding.detailSpinner.adapter = adapter
        binding.detailSpinner.setSelection(3, false)

        binding.detailSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {

                when (position) {
                    0 -> {
                        if (isSpinnerEventPossible) {
                            isSpinnerEventPossible = false
                            findNavController().navigate(R.id.action_detailFragment_to_editPostFragment)
                        }
                    }

                    1 -> {
                        isSpinnerEventPossible = false
                    }

                    2 -> {
                        if (isSpinnerEventPossible) {
                            isSpinnerEventPossible = false
                            findNavController().navigate(R.id.action_global_reportFragment)
                        }
                    }

                    else -> {

                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun setObserver() {
        viewModel.postDetailResponse.observe(viewLifecycleOwner) {
            it.result.apply {
                // TODO: 유저 조회 API 연동해서 프로필 이미지 같은거 표시해야 할듯
                Glide.with(this@DetailFragment).load(R.drawable.ic_profile)
                    .into(binding.detailImgProfile)
                binding.detailTxtPostTitle.text = title
                binding.detailTxtNickname.text = nickname
                binding.detailTxtInfo.text = "컴퓨터과학과 | 23학번"
                binding.detailTxtDetail.text = detail
                binding.detailTxtCurrentParticipant.text = "현재 채팅/참여중인 사람 ${inUserCount}명"
            }
        }
    }

}