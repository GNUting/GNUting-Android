package com.changs.android.gnuting_android.util

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.changs.android.gnuting_android.data.model.Member
import com.changs.android.gnuting_android.databinding.CurrentMemberBottomSheetBinding
import com.changs.android.gnuting_android.ui.adapter.PostCurrentMemberAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CurrentMemberBottomSheetFragment : BottomSheetDialogFragment() {
    private var _binding: CurrentMemberBottomSheetBinding? = null
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

        val adapter = PostCurrentMemberAdapter()
        binding.currentMemberBottomSheetRecyclerview.adapter = adapter

        val members = listOf(Member(null, "전재욱", "짱짱맨", "19학번", "25살", "ENTP", "안녕하세요 저는 컴퓨터과학과이고 컴퓨터과학을 공부하고 있습니다.", "asd123", "컴퓨터과학과"), Member(null, "전재욱", "짱짱맨", "19학번", "25살", "ENTP", "안녕하세요 저는 컴퓨터과학과이고 컴퓨터과학을 공부하고 있습니다.", "asd123", "컴퓨터과학과"), Member(null, "전재욱", "짱짱맨", "19학번", "25살", "ENTP", "안녕하세요 저는 컴퓨터과학과이고 컴퓨터과학을 공부하고 있습니다.", "asd123", "컴퓨터과학과"), Member(null, "전재욱", "짱짱맨", "19학번", "25살", "ENTP", "안녕하세요 저는 컴퓨터과학과이고 컴퓨터과학을 공부하고 있습니다.", "asd123", "컴퓨터과학과"))
        adapter.submitList(members)


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}