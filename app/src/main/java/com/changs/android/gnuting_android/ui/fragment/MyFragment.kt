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
import com.changs.android.gnuting_android.databinding.FragmentMyBinding

class MyFragment : BaseFragment<FragmentMyBinding>(FragmentMyBinding::bind, R.layout.fragment_my) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}