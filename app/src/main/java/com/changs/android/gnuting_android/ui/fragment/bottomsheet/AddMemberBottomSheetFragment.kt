package com.changs.android.gnuting_android.ui.fragment.bottomsheet

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.changs.android.gnuting_android.GNUApplication
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.data.model.InUser
import com.changs.android.gnuting_android.databinding.AddMemberBottomSheetBinding
import com.changs.android.gnuting_android.ui.MainActivity
import com.changs.android.gnuting_android.ui.adapter.PostCurrentMemberAdapter
import com.changs.android.gnuting_android.util.eventObserve
import com.changs.android.gnuting_android.viewmodel.HomeMainViewModel
import com.changs.android.gnuting_android.viewmodel.MemberAddViewModel
import com.changs.android.gnuting_android.viewmodel.PostViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi


@OptIn(ExperimentalCoroutinesApi::class)
@AndroidEntryPoint
class AddMemberBottomSheetFragment(
    private val memberAddViewModel: MemberAddViewModel, private val boardId: Int
) : BottomSheetDialogFragment() {
    private var _binding: AddMemberBottomSheetBinding? = null
    private val viewModel: HomeMainViewModel by activityViewModels()
    private val postViewModel: PostViewModel by viewModels()
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
            val searchMemberBottomSheetFragment =
                SearchMemberBottomSheetFragment(memberAddViewModel)
            searchMemberBottomSheetFragment.show(childFragmentManager, null)
        }

        binding.addMemberBottomSheetImgClose.setOnClickListener {
            dismiss()
        }

        binding.addMemberBottomSheetBtnChatRequest.setOnClickListener {
            memberAddViewModel.currentMember.value?.let {
                postViewModel.postApplyChat(boardId, it.toList())
            }
        }
    }

    private fun setObserver() {
        postViewModel.expirationToken.eventObserve(viewLifecycleOwner) {
            GNUApplication.sharedPreferences.edit().clear().apply()
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        postViewModel.spinner.observe(viewLifecycleOwner) { show ->
            binding.spinner.visibility = if (show) View.VISIBLE else View.GONE
        }

        postViewModel.toast.eventObserve(viewLifecycleOwner) { text ->
            text?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        memberAddViewModel.expirationToken.eventObserve(viewLifecycleOwner) {
            GNUApplication.sharedPreferences.edit().clear().apply()

            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
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

            memberAddViewModel.currentMember.value = mutableListOf(myUserInfo)
        }

        postViewModel.applyChatResponse.eventObserve(viewLifecycleOwner) {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        memberAddViewModel.currentMember.value = null
    }

    private fun navigateListener(user: InUser) {
        val args = bundleOf("user" to user)
        findNavController().navigate(R.id.photoFragment, args)
    }
}