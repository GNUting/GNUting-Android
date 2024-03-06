package com.changs.android.gnuting_android.ui.fragment

import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.databinding.FragmentJoin2Binding
import com.changs.android.gnuting_android.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Join2Fragment :
    BaseFragment<FragmentJoin2Binding>(FragmentJoin2Binding::bind, R.layout.fragment_join2) {
    private val viewModel: MainViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListener()
        setObserver()
    }

    private fun setListener() {
        binding.join2BtnNicknameCheck.setOnClickListener {
            viewModel.getCheckNickName()
        }

        binding.join2EditName.doAfterTextChanged {
            viewModel.name = it.toString()
        }

        binding.join2EditPhoneNumber.addTextChangedListener(PhoneNumberFormattingTextWatcher())

        binding.join2EditPhoneNumber.doOnTextChanged { text, start, before, count ->
            viewModel.phoneNumber = text.toString().replace("-", "")
        }

        binding.join2EditStudentId.doAfterTextChanged {
            viewModel.studentId = it.toString()
        }

        binding.join2EditNickname.doAfterTextChanged {
            viewModel.nickname = it.toString()
        }



        binding.join2EditIntro.doAfterTextChanged {
            viewModel.userSelfIntroduction = it.toString()
        }

        binding.join2RadioGroup.setOnCheckedChangeListener { _, checkedId ->
            viewModel.gender = when (checkedId) {
                R.id.join2_radio_male -> {
                    "MALE"
                }

                R.id.join2_radio_female -> {
                    "FEMALE"
                }

                else -> null
            }
        }

        binding.join2LlMajorContainer.setOnClickListener {
            val bottomDialogFragment = SearchDepartmentBottomSheetFragment()
            bottomDialogFragment.show(childFragmentManager, bottomDialogFragment.tag)
        }

        binding.join2LlBirthday.setOnClickListener {
            val newFragment = DatePickerFragment().apply {
                listener = fun(year: Int, month: Int, day: Int) {
                    with(binding) {
                        val calendar: Calendar = Calendar.getInstance()
                        calendar.set(year, month, day)

                        val format = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
                        val strDate: String = format.format(calendar.time)

                        join2TxtYear.text = year.toString()
                        join2TxtMonth.text = month.toString()
                        join2TxtDay.text = day.toString()

                        viewModel.birthDate = strDate
                    }
                }
            }
            newFragment.show(parentFragmentManager, tag)
        }

        binding.join2ImgBack.setOnClickListener { findNavController().popBackStack() }

        binding.join2BtnNext.setOnClickListener {
            viewModel.nickNameCheck.value?.let {
                if (it) {
                    if (viewModel.name == null || viewModel.phoneNumber == null || viewModel.gender == null || viewModel.department == null || viewModel.studentId == null || viewModel.userSelfIntroduction == null) {
                        Snackbar.make(binding.root, "입력되지 않은 항목이 있습니다.", Snackbar.LENGTH_SHORT)
                            .show()
                    } else {
                        val regex = Regex("^010\\d{7,8}$")
                        val isValid = regex.matches(viewModel.phoneNumber!!)

                        if (isValid) {
                            findNavController().navigate(R.id.action_join2Fragment_to_join3Fragment)
                        } else {
                            Snackbar.make(
                                binding.root, "전화번호 형식이 올바르지 않습니다.", Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    Snackbar.make(binding.root, "닉네임 인증이 완료되지 않았습니다.", Snackbar.LENGTH_SHORT).show()
                }
            } ?: Snackbar.make(binding.root, "닉네임 인증이 완료되지 않았습니다.", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun setObserver() {
        viewModel.choiceDepartment.observe(viewLifecycleOwner) {
            viewModel.department = it
            binding.join2TxtMajor.text = it
        }
    }
}