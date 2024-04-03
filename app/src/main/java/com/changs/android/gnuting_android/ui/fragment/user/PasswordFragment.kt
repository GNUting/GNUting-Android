package com.changs.android.gnuting_android.ui.fragment.user

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.databinding.FragmentPasswordBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PasswordFragment : BaseFragment<FragmentPasswordBinding>(FragmentPasswordBinding::bind, R.layout.fragment_password) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.passwordImgBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.passwordBtn.setOnClickListener {
            findNavController().navigate(R.id.action_passwordFragment_to_findPasswordFragment)
        }
    }
}