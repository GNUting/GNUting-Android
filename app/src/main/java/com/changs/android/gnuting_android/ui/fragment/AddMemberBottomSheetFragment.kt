package com.changs.android.gnuting_android.ui.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.data.model.InUser
import com.changs.android.gnuting_android.data.model.Member
import com.changs.android.gnuting_android.databinding.AddMemberBottomSheetBinding
import com.changs.android.gnuting_android.ui.adapter.AddMemberAdapter
import com.changs.android.gnuting_android.ui.adapter.PostCurrentMemberAdapter
import com.changs.android.gnuting_android.ui.adapter.SelectedMemberAdapter
import com.changs.android.gnuting_android.util.eventObserve
import com.changs.android.gnuting_android.viewmodel.HomeMainViewModel
import com.changs.android.gnuting_android.viewmodel.MemberAddViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.ExperimentalCoroutinesApi


@OptIn(ExperimentalCoroutinesApi::class)
class AddMemberBottomSheetFragment(private val memberAddViewModel: MemberAddViewModel, private val boardId: Int) : BottomSheetDialogFragment() {
    private var _binding: AddMemberBottomSheetBinding? = null
    private val viewModel: HomeMainViewModel by activityViewModels()
    private lateinit var adapter: PostCurrentMemberAdapter
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState)
        if (bottomSheetDialog is BottomSheetDialog) {
            bottomSheetDialog.behavior.skipCollapsed = true
            bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        return bottomSheetDialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = AddMemberBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListener()
        setRecyclerView()
        setObserver()
    }

    private fun setRecyclerView() {
        adapter = PostCurrentMemberAdapter()
        binding.addMemberBottomSheetRecyclerview.adapter = adapter

    }

    private fun setListener() {
        binding.addMemberBottomSheetLlAddMember.setOnClickListener {
            val searchMemberBottomSheetFragment = SearchMemberBottomSheetFragment(memberAddViewModel)
            searchMemberBottomSheetFragment.show(childFragmentManager, null)
        }

        binding.addMemberBottomSheetImgClose.setOnClickListener {
            dismiss()
        }

        binding.addMemberBottomSheetBtnChatRequest.setOnClickListener {
            memberAddViewModel.currentMember.value?.let {
                viewModel.postApplyChat(boardId, it.toList())
                dismiss()
            }
        }
    }

    private fun setObserver() {
        memberAddViewModel.currentMember.observe(viewLifecycleOwner) {
            binding.addMemberBottomSheetTxtMemberTitle.text = "멤버 (${it.size})"
            adapter.submitList(it)

        }
        viewModel.myInfo.value?.let { myInfo ->
            val myUserInfo = InUser(
                age = myInfo.age,
                department = myInfo.department,
                gender = myInfo.gender,
                id = myInfo.id,
                nickname = myInfo.nickname,
                profileImage = myInfo.profileImage,
                studentId = myInfo.studentId,
                userRole = myInfo.userRole,
                userSelfIntroduction = myInfo.userSelfIntroduction
            )

            memberAddViewModel.currentMember.value = mutableListOf(myUserInfo)
        }

        viewModel.applyChatResponse.eventObserve(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
    }
}