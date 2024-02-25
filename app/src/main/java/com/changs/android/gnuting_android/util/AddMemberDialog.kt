package com.changs.android.gnuting_android.util

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.changs.android.gnuting_android.data.model.Member
import com.changs.android.gnuting_android.databinding.DialogAddMemberBinding
import com.changs.android.gnuting_android.ui.adapter.AddMemberAdapter

class AddMemberDialog(val listener: () -> Unit) : DialogFragment() {

    private lateinit var binding: DialogAddMemberBinding

    override fun onResume() {
        super.onResume()

        dialog?.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val params = dialog?.window?.attributes
            params?.apply {
                width = (resources.displayMetrics.widthPixels * 0.9).toInt()
                height = ViewGroup.LayoutParams.WRAP_CONTENT
            }

            attributes = params
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DialogAddMemberBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        val adapter = AddMemberAdapter()
        binding.dialogAddMemberRecyclerview.adapter = adapter
        val members = listOf(Member(null, "전재욱", "짱짱맨", "19학번", "25살", "ENTP", "안녕하세요 저는 컴퓨터과학과이고 컴퓨터과학을 공부하고 있습니다.", "asd123", "컴퓨터과학과"))
        adapter.submitList(members)
    }
}