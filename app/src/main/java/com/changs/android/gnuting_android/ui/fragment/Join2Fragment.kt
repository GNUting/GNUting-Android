package com.changs.android.gnuting_android.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.databinding.FragmentJoin2Binding

class Join2Fragment :
    BaseFragment<FragmentJoin2Binding>(FragmentJoin2Binding::bind, R.layout.fragment_join2) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: Move to viewModel
        var gender: String? = null

        binding.join2RadioGroup.setOnCheckedChangeListener { _, checkedId ->
            gender = when (checkedId) {
                R.id.join2_radio_male -> {
                    "MALE"
                }

                R.id.join2_radio_female -> {
                    "FEMALE"
                }

                else -> null
            }
        }

        binding.join2LlBirthday.setOnClickListener {
            val newFragment = DatePickerFragment().apply {
                listener = fun(year: Int, month: Int, day: Int) {
                    with(binding) {
                        join2TxtYear.text = year.toString()
                        join2TxtMonth.text = month.toString()
                        join2TxtDay.text = day.toString()
                    }
                }
            }
            newFragment.show(parentFragmentManager, tag)
        }



        binding.join2ImgBack.setOnClickListener { findNavController().popBackStack() }

        binding.join2BtnNext.setOnClickListener {
            findNavController().navigate(R.id.action_join2Fragment_to_join3Fragment)
        }
    }


}