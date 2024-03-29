package com.changs.android.gnuting_android.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.databinding.FragmentPolicyBinding
import com.changs.android.gnuting_android.viewmodel.PolicyViewModel
import com.google.android.material.snackbar.Snackbar

class PolicyFragment :
    BaseFragment<FragmentPolicyBinding>(FragmentPolicyBinding::bind, R.layout.fragment_policy) {
    private val viewModel: PolicyViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fun isAllChecked() =
            binding.policyCheck1.isChecked && binding.policyCheck2.isChecked

        binding.policyImgBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.policyBtnNext.setOnClickListener {
            if (binding.policyCheck1.isChecked && binding.policyCheck2.isChecked) findNavController().navigate(
                R.id.action_policyFragment_to_join1Fragment
            )
            else Snackbar.make(binding.root, "필수 항목을 모두 체크해주세요.", Snackbar.LENGTH_SHORT).show()
        }

        viewModel.isAllChecked.observe(viewLifecycleOwner) {
            with(binding) {
                policyCheckAll.isChecked = it
                policyCheck1.isChecked = it
                policyCheck2.isChecked = it
            }
        }

        binding.policyCheck1.setOnClickListener {
            binding.policyCheckAll.isChecked = isAllChecked()
        }

        binding.policyCheck2.setOnClickListener {
            binding.policyCheckAll.isChecked = isAllChecked()
        }

        binding.policyCheckAll.setOnClickListener {
            viewModel.isAllChecked.value = binding.policyCheckAll.isChecked
        }


    }

    override fun onPause() {
        super.onPause()
        viewModel.isAllChecked.value = false
    }
}