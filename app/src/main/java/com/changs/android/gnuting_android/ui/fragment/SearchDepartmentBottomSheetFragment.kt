package com.changs.android.gnuting_android.ui.fragment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.changs.android.gnuting_android.data.model.Member
import com.changs.android.gnuting_android.databinding.CurrentMemberBottomSheetBinding
import com.changs.android.gnuting_android.databinding.SearchDepartmentBottomSheetBinding
import com.changs.android.gnuting_android.ui.adapter.DepartmentAdapter
import com.changs.android.gnuting_android.ui.adapter.PostCurrentMemberAdapter
import com.changs.android.gnuting_android.viewmodel.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchDepartmentBottomSheetFragment : BottomSheetDialogFragment() {
    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: SearchDepartmentBottomSheetBinding? = null
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
        _binding = SearchDepartmentBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getSearchDepartment("")

        binding.searchDepartmentBottomSheetEditSearch.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    viewModel.getSearchDepartment(binding.searchDepartmentBottomSheetEditSearch.text.toString())
                    return true
                }
                return false
            }
        })

        binding.searchDepartmentBottomSheetImgClose.setOnClickListener {
            dismiss()
        }

        val adapter = DepartmentAdapter {
            viewModel.choiceDepartment.value = it
            dismiss()
        }
        binding.searchDepartmentBottomSheetRecyclerview.adapter = adapter

        viewModel.searchDepartmentResponse.observe(viewLifecycleOwner) {
            adapter.submitList(it.result)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}