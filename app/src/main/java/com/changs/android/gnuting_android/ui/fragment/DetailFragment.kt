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
import com.changs.android.gnuting_android.data.model.InUser
import com.changs.android.gnuting_android.databinding.FragmentDetailBinding
import com.changs.android.gnuting_android.ui.adapter.SpinnerAdapter
import com.changs.android.gnuting_android.util.eventObserve
import com.changs.android.gnuting_android.viewmodel.DetailViewModel
import com.changs.android.gnuting_android.viewmodel.HomeMainViewModel
import com.changs.android.gnuting_android.viewmodel.MemberAddViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class DetailFragment :
    BaseFragment<FragmentDetailBinding>(FragmentDetailBinding::bind, R.layout.fragment_detail) {
    private val viewModel: HomeMainViewModel by activityViewModels()
    private val memberAddViewModel: MemberAddViewModel by viewModels { MemberAddViewModel.Factory }
    private val detailViewModel: DetailViewModel by viewModels()
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
    }

    private fun setSpinner() {
        binding.detailImgSetting.setOnClickListener {
            binding.detailSpinner.performClick()
            detailViewModel.isSpinnerEventPossible = true
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
                        if (detailViewModel.isSpinnerEventPossible) {
                            detailViewModel.isSpinnerEventPossible = false
                            val action =
                                DetailFragmentDirections.actionDetailFragmentToEditPostFragment(args.id)
                            findNavController().navigate(action)
                        }
                    }

                    1 -> {
                        if (detailViewModel.isSpinnerEventPossible) {
                            viewModel.deletePost(args.id)
                        }
                    }

                    2 -> {
                        if (detailViewModel.isSpinnerEventPossible) {
                            detailViewModel.isSpinnerEventPossible = false
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

        binding.detailBtnChatRequest.setOnClickListener {
            val addMemberBottomSheetFragment = AddMemberBottomSheetFragment(memberAddViewModel, args.id)
            addMemberBottomSheetFragment.show(childFragmentManager, null)
        }
    }

    private fun setObserver() {
        viewModel.deletePostResponse.eventObserve(viewLifecycleOwner) {
            Snackbar.make(binding.root, it.result, Snackbar.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
        viewModel.postDetailResponse.observe(viewLifecycleOwner) {
            it.result.apply {
                viewModel.myInfo.value?.let { myInfo ->
                    if (myInfo.nickname == user.nickname) {
                        binding.detailBtnChatRequest.visibility = View.INVISIBLE
                        binding.detailSpinner.visibility = View.INVISIBLE
                        binding.detailImgSetting.visibility = View.VISIBLE
                    }
                }

                Glide.with(this@DetailFragment).load(user.profileImage).error(R.drawable.ic_profile)
                    .into(binding.detailImgProfile)
                binding.detailTxtPostTitle.text = title
                binding.detailTxtNickname.text = user.nickname
                binding.detailTxtInfo.text = "${user.department} | ${user.studentId}"
                binding.detailTxtDetail.text = detail
                binding.detailTxtCurrentParticipant.text = "현재 채팅/참여중인 사람 ${inUser.size}명"
                binding.detailTxtTime.text = time

                binding.detailTxtCurrentParticipant.setOnClickListener {
                    val bottomDialogFragment = CurrentMemberBottomSheetFragment(inUser)
                    bottomDialogFragment.show(childFragmentManager, bottomDialogFragment.tag)
                }
            }
        }
    }

}