package com.changs.android.gnuting_android.ui.fragment.application

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.data.model.InUser
import com.changs.android.gnuting_android.databinding.FragmentApplicationBinding
import com.changs.android.gnuting_android.ui.HomeActivity
import com.changs.android.gnuting_android.ui.adapter.ApplicationMemberAdapter
import com.changs.android.gnuting_android.util.eventObserve
import com.changs.android.gnuting_android.util.showOneButtonDialog
import com.changs.android.gnuting_android.viewmodel.ApplicationViewModel
import com.changs.android.gnuting_android.viewmodel.HomeMainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi


@OptIn(ExperimentalCoroutinesApi::class)
@AndroidEntryPoint
class ApplicationFragment : BaseFragment<FragmentApplicationBinding>(
    FragmentApplicationBinding::bind,
    R.layout.fragment_application
) {
    private val args: ApplicationFragmentArgs by navArgs()
    private val applicationViewModel: ApplicationViewModel by viewModels()
    private val viewModel: HomeMainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applicationViewModel.getApplicationDetail(args.id)
        setObserver()

        binding.applicationImgBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setObserver() {
        applicationViewModel.dialog.eventObserve(viewLifecycleOwner) { message ->
            message?.let {
                showOneButtonDialog(context = requireContext(),  titleText = it, action = {
                    findNavController().popBackStack()
                })
            }
        }

        applicationViewModel.applicationDetailResponse.observe(viewLifecycleOwner) {
            it.result.run {
                binding.applicationCardRecyclerviewContainer1.visibility = View.VISIBLE
                binding.applicationCardRecyclerviewContainer2.visibility = View.VISIBLE
                binding.applicationTxtMember.visibility = View.VISIBLE

                binding.applicationTxtMember.text = "$applyUserCount : $applyUserCount 매칭"

                when (applyStatus) {
                    "대기중" -> {
                        binding.applicationTxtStatus.text = "대기중"
                        binding.applicationTxtStatus.setTextColor(resources.getColor(R.color.gray7, null))

                        viewModel.myInfo.value?.let { myInfo ->
                            val none = applyUser.none { it.id == myInfo.id }
                            if (none) {
                                binding.applicationBtnRefuse.visibility = View.VISIBLE
                                binding.applicationBtnAccept.visibility = View.VISIBLE

                                binding.applicationBtnAccept.setOnClickListener {
                                    applicationViewModel.accept(id)
                                }

                                binding.applicationBtnRefuse.setOnClickListener {
                                    applicationViewModel.refuse(id)
                                }
                            } else {
                                binding.applicationBtnCancel.visibility = View.VISIBLE

                                binding.applicationBtnCancel.setOnClickListener {
                                    applicationViewModel.cancel(id)
                                }
                            }
                        }

                    }

                    "거절" -> {
                        binding.applicationTxtStatus.text = "거절 완료"
                        binding.applicationTxtStatus.setTextColor(resources.getColor(R.color.main, null))
                    }

                    else -> {
                        binding.applicationTxtStatus.text = "수락 완료"
                        binding.applicationTxtStatus.setTextColor(resources.getColor(R.color.secondary, null))
                    }
                }

                val adapter1 = ApplicationMemberAdapter(::navigateListener)
                val adapter2 = ApplicationMemberAdapter(::navigateListener)

                binding.applicationTxtRecyclerviewHeader1.text = "▶ $applyUserDepartment"
                binding.applicationTxtRecyclerviewHeader2.text = "◀ $participantUserDepartment"

                binding.applicationRecyclerview1.adapter = adapter1
                binding.applicationRecyclerview2.adapter = adapter2

                adapter1.submitList(applyUser)
                adapter2.submitList(participantUser)
            }
        }

        applicationViewModel.spinner.observe(viewLifecycleOwner) { show ->
            binding.spinner.visibility = if (show) View.VISIBLE else View.GONE
        }

        applicationViewModel.toast.eventObserve(viewLifecycleOwner) { text ->
            text?.let {
                (requireActivity() as HomeActivity).showToast(it)
            }
        }

        applicationViewModel.cancelResponse.eventObserve(viewLifecycleOwner) {
            findNavController().popBackStack()
        }

        applicationViewModel.acceptResponse.eventObserve(viewLifecycleOwner) {
            findNavController().popBackStack()
        }

        applicationViewModel.refuseResponse.eventObserve(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
    }

    private fun navigateListener(user: InUser) {
        val args = bundleOf("user" to user)
        findNavController().navigate(R.id.photoFragment, args)
    }

}