package com.changs.android.gnuting_android.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.data.model.Member
import com.changs.android.gnuting_android.databinding.FragmentApplicationBinding
import com.changs.android.gnuting_android.databinding.FragmentListBinding
import com.changs.android.gnuting_android.ui.adapter.ApplicationMemberAdapter
import com.changs.android.gnuting_android.util.eventObserve
import com.changs.android.gnuting_android.viewmodel.HomeMainViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi


@OptIn(ExperimentalCoroutinesApi::class)
class ApplicationFragment : BaseFragment<FragmentApplicationBinding>(
    FragmentApplicationBinding::bind,
    R.layout.fragment_application
) {
    private val args: ApplicationFragmentArgs by navArgs()
    private val viewModel: HomeMainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setObserver()

        args.applicationItem.run {
            binding.applicationTxtDepartment.text = applyUserDepartment
            binding.applicationTxtMemberCount.text = "${applyUserCount}명"

            when (applyStatus) {
                "대기중" -> {
                    binding.applicationTxtStatus.text = "대기중"
                    binding.applicationTxtStatus.setBackgroundResource(R.drawable.background_radius_10dp_solid_gray7)

                    viewModel.myInfo.value?.let { myInfo ->
                        val none = args.applicationItem.applyUser.none { it.id == myInfo.id }
                        if (none) {
                            binding.applicationBtnLeft.visibility = View.VISIBLE
                            binding.applicationBtnRight.visibility = View.VISIBLE

                            binding.applicationBtnLeft.setOnClickListener {
                                viewModel.accept(id)
                            }

                            binding.applicationBtnRight.setOnClickListener {
                                viewModel.refuse(id)
                            }
                        } else {
                            binding.applicationBtnCancel.visibility = View.VISIBLE

                            binding.applicationBtnCancel.setOnClickListener {
                                viewModel.cancel(id)
                            }
                        }
                    }


                }

                "거절" -> {
                    binding.applicationTxtStatus.text = "거절됨"
                    binding.applicationTxtStatus.setBackgroundResource(R.drawable.background_radius_10dp_solid_main)
                }

                else -> {
                    binding.applicationTxtStatus.text = "수락"
                    binding.applicationTxtStatus.setBackgroundResource(R.drawable.background_radius_10dp_solid_secondary)
                }
            }

            val adapter1 = ApplicationMemberAdapter()
            val adapter2 = ApplicationMemberAdapter()

            binding.applicationTxtRecyclerviewHeader1.text = applyUserDepartment
            binding.applicationTxtRecyclerviewHeader2.text = participantUserDepartment

            binding.applicationRecyclerview1.adapter = adapter1
            binding.applicationRecyclerview2.adapter = adapter2

            adapter1.submitList(applyUser)
            adapter2.submitList(participantUser)
        }

        binding.applicationImgBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setObserver() {
        viewModel.cancelResponse.eventObserve(viewLifecycleOwner) {
            findNavController().popBackStack()
        }

        viewModel.acceptResponse.eventObserve(viewLifecycleOwner) {
            findNavController().popBackStack()
        }

        viewModel.refuseResponse.eventObserve(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
    }


}