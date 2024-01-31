package com.changs.android.gnuting_android.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.databinding.FragmentJoin1Binding

class Join1Fragment : BaseFragment<FragmentJoin1Binding>(FragmentJoin1Binding::bind, R.layout.fragment_join1) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.join1ImgBack.setOnClickListener { findNavController().popBackStack() }

        binding.join1BtnNext.setOnClickListener {
            findNavController().navigate(R.id.action_join1Fragment_to_join2Fragment)
        }
    }
}