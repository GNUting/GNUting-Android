package com.changs.android.gnuting_android.ui.fragment

import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.databinding.FragmentJoin2Binding
import com.changs.android.gnuting_android.viewmodel.ButtonActiveCheckViewModel
import com.changs.android.gnuting_android.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Join2Fragment :
    BaseFragment<FragmentJoin2Binding>(FragmentJoin2Binding::bind, R.layout.fragment_join2) {
    private val viewModel: MainViewModel by activityViewModels()
    private val buttonActiveCheckViewModel: ButtonActiveCheckViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListener()
        setObserver()
    }

    private fun setListener() {
        binding.join2BtnNicknameCheck.setOnClickListener {
            viewModel.getCheckNickName(binding.join2EditNickname.text.toString())
        }

        binding.join2EditName.doOnTextChanged { text, start, count, after ->
            viewModel.name = text.toString()
            checkButtonActiveCondition()
        }

        binding.join2EditPhoneNumber.addTextChangedListener(PhoneNumberFormattingTextWatcher())

        binding.join2EditPhoneNumber.doOnTextChanged { text, start, before, count ->
            viewModel.phoneNumber = text.toString()
            checkButtonActiveCondition()
        }

        binding.join2EditStudentId.doOnTextChanged { text, start, count, after ->
            viewModel.studentId = text.toString()
            checkButtonActiveCondition()
        }

        binding.join2EditNickname.doOnTextChanged { text, start, count, after ->
            if (!text.isNullOrEmpty()) {
                binding.join2BtnNicknameCheck.setBackgroundResource(R.drawable.background_radius_10dp_solid_main)
                binding.join2BtnNicknameCheck.isEnabled = true
            } else {
                binding.join2BtnNicknameCheck.setBackgroundResource(R.drawable.background_radius_10dp_solid_gray7)
                binding.join2BtnNicknameCheck.isEnabled = false
            }

            checkButtonActiveCondition()
        }



        binding.join2EditIntro.doOnTextChanged { text, start, count, after ->
            viewModel.userSelfIntroduction = text.toString()
            checkButtonActiveCondition()
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
            checkButtonActiveCondition()
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
                        checkButtonActiveCondition()
                    }
                }
            }
            newFragment.show(parentFragmentManager, tag)
        }

        binding.join2ImgBack.setOnClickListener { findNavController().popBackStack() }

        binding.join2BtnNext.setOnClickListener {
            viewModel.nickNameCheck.value?.let {
                if (it && !viewModel.nickname.isNullOrEmpty() && binding.join2EditNickname.text.toString() == viewModel.nickname) {
                    if (viewModel.name == null || viewModel.phoneNumber == null || viewModel.gender == null || viewModel.department == null || viewModel.studentId == null) {
                        Snackbar.make(binding.root, "입력되지 않은 항목이 있습니다.", Snackbar.LENGTH_SHORT)
                            .show()
                    } else {
                        val regex = Regex("^\\d{3}-\\d{4}-\\d{4}$")
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
            binding.join2TxtMajor.setTextColor(resources.getColor(R.color.black, null))
            binding.join2TxtMajor.text = it
        }

        buttonActiveCheckViewModel.buttonActiveCheck.observe(viewLifecycleOwner) {
            if (it) {
                binding.join2BtnNext.setBackgroundResource(R.drawable.background_radius_10dp_solid_main)
                binding.join2BtnNext.isEnabled = true
            } else {
                binding.join2BtnNext.setBackgroundResource(R.drawable.background_radius_10dp_solid_gray7)
                binding.join2BtnNext.isEnabled = false
            }
        }

        viewModel.nickNameCheck.observe(viewLifecycleOwner) {
            checkButtonActiveCondition()
        }
    }

    private fun checkButtonActiveCondition() {
        buttonActiveCheckViewModel.buttonActiveCheck.value =
            (viewModel.nickNameCheck.value?: false && !viewModel.name.isNullOrEmpty() && !viewModel.phoneNumber.isNullOrEmpty() && !viewModel.nickname.isNullOrEmpty() && !viewModel.gender.isNullOrEmpty() && !viewModel.birthDate.isNullOrEmpty() && !viewModel.studentId.isNullOrEmpty())
    }
}