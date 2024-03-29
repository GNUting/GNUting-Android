package com.changs.android.gnuting_android.ui.fragment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.data.model.InUser
import com.changs.android.gnuting_android.data.model.Member
import com.changs.android.gnuting_android.databinding.CurrentMemberBottomSheetBinding
import com.changs.android.gnuting_android.ui.adapter.PostCurrentMemberAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CurrentMemberBottomSheetFragment(private val currentMember: List<InUser>) : BottomSheetDialogFragment() {
    private var _binding: CurrentMemberBottomSheetBinding? = null
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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CurrentMemberBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.currentMemberBottomSheetImgClose.setOnClickListener {
            dismiss()
        }

        val adapter = PostCurrentMemberAdapter(::navigateListener)
        binding.currentMemberBottomSheetRecyclerview.adapter = adapter
        adapter.submitList(currentMember)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun navigateListener(user: InUser) {
        val args = bundleOf("user" to user)
        findNavController().navigate(R.id.photoFragment, args)
    }

}