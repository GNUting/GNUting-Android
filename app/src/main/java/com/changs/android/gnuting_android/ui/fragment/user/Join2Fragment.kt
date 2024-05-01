package com.changs.android.gnuting_android.ui.fragment.user

import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.InputFilter
import android.view.View
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.databinding.FragmentJoin2Binding
import com.changs.android.gnuting_android.ui.fragment.bottomsheet.SearchDepartmentBottomSheetFragment
import com.changs.android.gnuting_android.viewmodel.ButtonActiveCheckViewModel
import com.changs.android.gnuting_android.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class Join2Fragment :
    BaseFragment<FragmentJoin2Binding>(FragmentJoin2Binding::bind, R.layout.fragment_join2) {
    private val viewModel: MainViewModel by activityViewModels()
    private val buttonActiveCheckViewModel: ButtonActiveCheckViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.profileImage = null

        if (viewModel.gender != null) {
            when (viewModel.gender) {
                "MALE" -> {
                    binding.join2RadioMale.isChecked = true
                }

                "FEMALE" -> {
                    binding.join2RadioFemale.isChecked = true
                }
            }
        }


        if (viewModel.birthDate != null) {
            try {
                val (year, month, day) = viewModel.birthDate!!.split("-")

                with(binding) {
                    join2TxtYear.text = year.toString()
                    join2TxtMonth.text = month.toString()
                    join2TxtDay.text = day.toString()
                }
            } catch (e: Exception) {
                Timber.e(e.message)
            }
        }
        setListener()
        setObserver()

        with(viewModel) {
            Timber.tag("회원가입")
                .d("nickName: ${nickname}, phoneNumber: ${phoneNumber}, gender: ${gender}, department: $department, studentId: ${studentId}")
        }
    }

    private fun setListener() {
        binding.join2EditName.filters = arrayOf<InputFilter>(
            InputFilter.LengthFilter(8)
        )

        binding.join2EditNickname.filters = arrayOf<InputFilter>(
            InputFilter.LengthFilter(10)
        )

        binding.join2EditIntro.filters = arrayOf<InputFilter>(
            InputFilter.LengthFilter(30)
        )

        /* 닉네임 중복 확인 버튼
        * 1. 기존 닉네임 중복 확인 상태를 초기화해야 함
        * */
        binding.join2BtnNicknameCheck.setOnClickListener {
            viewModel.getCheckNickName(binding.join2EditNickname.text.toString())
            binding.join2TxtVerificationNickname.visibility = View.INVISIBLE
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

        /* 닉네임 입력창
        * 1. 입력이 변하면 중복 확인 정보를 초기화시켜야 함
        * */
        binding.join2EditNickname.doOnTextChanged { text, start, count, after ->
            if (viewModel.nickname != binding.join2EditNickname.text.toString()) viewModel.nickNameCheck.value =
                null

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

        /* 다음 버튼
        * 1. 회원가입에 필요한 뷰모델 필수 정보, 뷰에 표시된 정보가 일치해야 하며, 누락된 것이 없어야 함
        * */
        binding.join2BtnNext.setOnClickListener {
            viewModel.nickNameCheck.value?.let {
                if (it && !viewModel.nickname.isNullOrEmpty() && binding.join2EditNickname.text.toString() == viewModel.nickname) {
                    if (viewModel.name == null || viewModel.phoneNumber == null || viewModel.gender == null || viewModel.department == null || viewModel.studentId == null) {
                        Toast.makeText(requireContext(), "입력되지 않은 항목이 있습니다.", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        val regex = Regex("^\\d{3}-\\d{4}-\\d{4}$")
                        val isValid = regex.matches(viewModel.phoneNumber!!)

                        if (isValid) {
                            findNavController().navigate(R.id.action_join2Fragment_to_join3Fragment)
                        } else {
                            Toast.makeText(
                                requireContext(), "전화번호 형식이 올바르지 않습니다.", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "닉네임 인증이 완료되지 않았습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            } ?: Toast.makeText(requireContext(), "닉네임 인증이 완료되지 않았습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setObserver() {
        viewModel.choiceDepartment.observe(viewLifecycleOwner) {
            viewModel.department = it
            binding.join2TxtMajor.setTextColor(resources.getColor(R.color.black, null))
            binding.join2TxtMajor.text = it
            checkButtonActiveCondition()
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

        viewModel.nickNameCheck.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess == true) {
                binding.join2TxtVerificationNickname.text = "사용할 수 있는 닉네임 입니다."
                binding.join2TxtVerificationNickname.setTextColor(
                    resources.getColor(
                        R.color.secondary, null
                    )
                )
                binding.join2TxtVerificationNickname.visibility = View.VISIBLE
            } else if (isSuccess == false) {
                binding.join2TxtVerificationNickname.text = "중복된 닉네임 입니다."
                binding.join2TxtVerificationNickname.setTextColor(
                    resources.getColor(
                        R.color.main, null
                    )
                )
                binding.join2TxtVerificationNickname.visibility = View.VISIBLE
            } else {
                binding.join2TxtVerificationNickname.visibility = View.INVISIBLE
            }
            checkButtonActiveCondition()
        }
    }

    private fun checkButtonActiveCondition() {
        buttonActiveCheckViewModel.buttonActiveCheck.value =
            (viewModel.nickNameCheck.value ?: false && !viewModel.name.isNullOrEmpty() && !viewModel.phoneNumber.isNullOrEmpty() && !viewModel.nickname.isNullOrEmpty() && !viewModel.gender.isNullOrEmpty() && !viewModel.birthDate.isNullOrEmpty() && !viewModel.studentId.isNullOrEmpty() && !viewModel.department.isNullOrEmpty())
    }
}