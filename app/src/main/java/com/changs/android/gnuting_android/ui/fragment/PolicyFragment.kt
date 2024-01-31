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

class PolicyFragment :
    BaseFragment<FragmentPolicyBinding>(FragmentPolicyBinding::bind, R.layout.fragment_policy) {
    private val viewModel: PolicyViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fun isAllChecked() =
            binding.policyCheck1.isChecked && binding.policyCheck2.isChecked && binding.policyCheck3.isChecked && binding.policyCheck4.isChecked && binding.policyCheck5.isChecked

        binding.policyImgBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.policyBtnNext.setOnClickListener {
            findNavController().navigate(R.id.action_policyFragment_to_join1Fragment)
        }

        viewModel.isAllChecked.observe(viewLifecycleOwner) {
            with(binding) {
                policyCheckAll.isChecked = it
                policyCheck1.isChecked = it
                policyCheck2.isChecked = it
                policyCheck3.isChecked = it
                policyCheck4.isChecked = it
                policyCheck5.isChecked = it
            }
        }

        binding.policyCheck1.setOnClickListener {
            binding.policyCheckAll.isChecked = isAllChecked()
        }

        binding.policyCheck2.setOnClickListener {
            binding.policyCheckAll.isChecked = isAllChecked()
        }

        binding.policyCheck3.setOnClickListener {
            binding.policyCheckAll.isChecked = isAllChecked()
        }

        binding.policyCheck4.setOnClickListener {
            binding.policyCheckAll.isChecked = isAllChecked()
        }

        binding.policyCheck5.setOnClickListener {
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