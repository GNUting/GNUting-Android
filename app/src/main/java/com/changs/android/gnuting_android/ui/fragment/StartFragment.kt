package com.changs.android.gnuting_android.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.databinding.FragmentStartBinding



class StartFragment : BaseFragment<FragmentStartBinding>(FragmentStartBinding::bind, R.layout.fragment_start) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.startBtnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_startFragment_to_loginFragment)
        }
    }
}