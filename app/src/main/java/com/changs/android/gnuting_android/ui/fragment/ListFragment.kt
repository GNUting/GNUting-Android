package com.changs.android.gnuting_android.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.databinding.FragmentListBinding
import com.changs.android.gnuting_android.databinding.FragmentMyBinding

class ListFragment : BaseFragment<FragmentListBinding>(FragmentListBinding::bind, R.layout.fragment_list) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}