package com.changs.android.gnuting_android.ui.fragment.bottomsheet

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.data.model.InUser
import com.changs.android.gnuting_android.databinding.AddMemberBottomSheetBinding
import com.changs.android.gnuting_android.ui.HomeActivity
import com.changs.android.gnuting_android.ui.adapter.PostCurrentMemberAdapter
import com.changs.android.gnuting_android.util.eventObserve
import com.changs.android.gnuting_android.viewmodel.HomeMainViewModel
import com.changs.android.gnuting_android.viewmodel.MemberAddViewModel
import com.changs.android.gnuting_android.viewmodel.PostViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi


@OptIn(ExperimentalCoroutinesApi::class)
@AndroidEntryPoint
class AddMemberBottomSheetFragment : BottomSheetDialogFragment() {
    private val args: AddMemberBottomSheetFragmentArgs by navArgs()
    private var _binding: AddMemberBottomSheetBinding? = null
    private val viewModel: HomeMainViewModel by activityViewModels()
    private val postViewModel: PostViewModel by viewModels()
    private val memberAddViewModel: MemberAddViewModel by hiltNavGraphViewModels(R.id.detail_graph)
    private lateinit var adapter: PostCurrentMemberAdapter
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setOnShowListener {

            val bottomSheetDialog = it as BottomSheetDialog
            bottomSheetDialog.behavior.skipCollapsed = true

            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

            parentLayout?.let {
                val behaviour = BottomSheetBehavior.from(it)
                setupFullHeight(it)
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return dialog
    }

    override fun getTheme(): Int = R.style.NoMarginsDialog


    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
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
        adapter = PostCurrentMemberAdapter(::navigateListener)
        binding.addMemberBottomSheetRecyclerview.adapter = adapter

    }

    private fun setListener() {
        binding.addMemberBottomSheetLlAddMember.setOnClickListener {
            findNavController().navigate(R.id.action_addMemberBottomSheetFragment_to_searchMemberBottomSheetFragment)
        }

        binding.addMemberBottomSheetImgClose.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.addMemberBottomSheetBtnChatRequest.setOnClickListener {
            memberAddViewModel.currentMember.value?.let {
                postViewModel.postApplyChat(args.boardId, it.toList())
            }
        }
    }

    private fun setObserver() {
        postViewModel.spinner.observe(viewLifecycleOwner) { show ->
            binding.spinner.visibility = if (show) View.VISIBLE else View.GONE
        }

        postViewModel.toast.eventObserve(viewLifecycleOwner) { text ->
            text?.let {
                (requireActivity() as HomeActivity).showToast(it)
            }
        }

        memberAddViewModel.currentMember.observe(viewLifecycleOwner) {
            it?.let {
                binding.addMemberBottomSheetTxtMemberTitle.text = "멤버 (${it.size})"
                adapter.submitList(it)
            }

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

            if (memberAddViewModel.currentMember.value == null) {
                memberAddViewModel.currentMember.value = mutableListOf(myUserInfo)
            }
        }

        postViewModel.applyChatResponse.eventObserve(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun navigateListener(user: InUser) {
        val args = bundleOf("user" to user)
        findNavController().navigate(
            R.id.action_addMemberBottomSheetFragment_to_photoFragment3,
            args
        )
    }
}