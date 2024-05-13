package com.changs.android.gnuting_android.ui.fragment.user

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.databinding.FragmentPolicyBinding
import com.changs.android.gnuting_android.viewmodel.ButtonActiveCheckViewModel
import com.changs.android.gnuting_android.viewmodel.MainViewModel
import com.changs.android.gnuting_android.viewmodel.PolicyViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PolicyFragment :
    BaseFragment<FragmentPolicyBinding>(FragmentPolicyBinding::bind, R.layout.fragment_policy) {
    private val policyViewModel: PolicyViewModel by viewModels()
    private val viewModel: MainViewModel by activityViewModels()
    private val buttonActiveCheckViewModel: ButtonActiveCheckViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(viewModel) {
            email = null
            password = null
        }

        binding.policyImgBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.policyTxtPrivacyPolicy.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://gnuting.github.io/GNUting-PrivacyPolicy/privacy_policy")
            )
            startActivity(intent)
        }

        binding.policyTxtTermsOfService.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://equal-kiwi-602.notion.site/9021bea8cf1841fc8a83d26a06c8e72c?pvs=4")
            )
            startActivity(intent)
        }

        binding.policyBtnNext.setOnClickListener {
            if (binding.policyCheck1.isChecked && binding.policyCheck2.isChecked && binding.policyCheck3.isChecked) findNavController().navigate(
                R.id.action_policyFragment_to_join1Fragment
            )
            else Toast.makeText(requireContext(), "필수 항목을 모두 체크해주세요.", Toast.LENGTH_SHORT).show()
        }

        policyViewModel.isAllChecked.observe(viewLifecycleOwner) {
            with(binding) {
                policyCheckAll.isChecked = it
                policyCheck1.isChecked = it
                policyCheck2.isChecked = it
                policyCheck3.isChecked = it
            }

            buttonActiveCheckViewModel.buttonActiveCheck.value = it
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

        binding.policyCheckAll.setOnClickListener {
            policyViewModel.isAllChecked.value = binding.policyCheckAll.isChecked
        }

        buttonActiveCheckViewModel.buttonActiveCheck.observe(viewLifecycleOwner) {
            if (it) {
                binding.policyBtnNext.setBackgroundResource(R.drawable.background_radius_10dp_solid_main)
                binding.policyBtnNext.isEnabled = true
            } else {
                binding.policyBtnNext.setBackgroundResource(R.drawable.background_radius_10dp_solid_gray7)
                binding.policyBtnNext.isEnabled = false
            }
        }
    }

    private fun isAllChecked(): Boolean {
        val isAllChecked =
            binding.policyCheck1.isChecked && binding.policyCheck2.isChecked && binding.policyCheck3.isChecked
        buttonActiveCheckViewModel.buttonActiveCheck.value = isAllChecked
        return isAllChecked
    }
}