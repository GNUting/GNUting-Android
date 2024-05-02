package com.changs.android.gnuting_android.ui.fragment.user

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.databinding.FragmentJoin1Binding
import com.changs.android.gnuting_android.util.convertMillisecondsToTime
import com.changs.android.gnuting_android.util.eventObserve
import com.changs.android.gnuting_android.util.hideSoftKeyboard
import com.changs.android.gnuting_android.viewmodel.ButtonActiveCheckViewModel
import com.changs.android.gnuting_android.viewmodel.CertificationViewModel
import com.changs.android.gnuting_android.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class Join1Fragment :
    BaseFragment<FragmentJoin1Binding>(FragmentJoin1Binding::bind, R.layout.fragment_join1) {
    private val viewModel: MainViewModel by activityViewModels()
    private val certificationViewModel: CertificationViewModel by viewModels()
    private val buttonActiveCheckViewModel: ButtonActiveCheckViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(viewModel) {
            name = null
            phoneNumber = null
            gender = null
            nickname = null
            birthDate = null
            department = null
            studentId = null
            userSelfIntroduction = null
            nickNameCheck.value = null
        }

        with(viewModel) {
            Timber.tag("회원가입").d("리스너, 옵저버 실행 전 email: ${viewModel.email}, password: ${viewModel.password}, mailCertificationNumberCheck: ${certificationViewModel.mailCertificationNumberCheck}, mailCertificationNumber :${viewModel.mailCertificationNumber.value?.getContentIfNotHandled().toString()}, emailVerifyResponse :${viewModel.emailVerifyResponse.value}")
        }

        setListener()
        setObserver()

        with(viewModel) {
            Timber.tag("회원가입").d("리스너, 옵저버 실행 후 email: ${viewModel.email}, password: ${viewModel.password}, mailCertificationNumberCheck: ${certificationViewModel.mailCertificationNumberCheck}, mailCertificationNumber :${viewModel.mailCertificationNumber.value?.getContentIfNotHandled().toString()}, emailVerifyResponse :${viewModel.emailVerifyResponse.value}")
        }
    }

    private fun setListener() {
        binding.join1BtnCertificationConfirmation.setOnClickListener {
            if (certificationViewModel.timerCount.value == 0L) {
                binding.join1TxtVerificationCertification.text = "인증 시간이 초과되었습니다."
                binding.join1TxtVerificationCertification.setTextColor(
                    resources.getColor(
                        R.color.main, null
                    )
                )
                binding.join1TxtVerificationCertification.visibility = View.VISIBLE
            } else {
                val certificationNumber = binding.join1EditCertificationNumber.text.toString()
                viewModel.postEmailVerify(certificationNumber)
            }
        }

        /* 이메일 인증 번호 받기
        * 1. 인증 받기를 다시 눌렀으므로 기존에 인증번호 확인을 했던 과정들, 인증을 진행 중이던 것 모두 초기화시켜야 함
        * */
        binding.join1BtnVerify.setOnClickListener {
            binding.join1TxtVerificationCertification.visibility = View.INVISIBLE
            viewModel.postMailCertification()
        }

        /* 이메일 입력창
        1. 입력이 변하면 인증을 받아야 함
        2. 인증을 받아야 하므로 인증번호 확인을 했던 과정들, 인증을 진행 중이던 것 모두 초기화시켜야 함
        * */
        binding.join1EditEmail.doOnTextChanged { text, start, count, after ->
            if (viewModel.email != text.toString() + "@gnu.ac.kr") {
                // 타이머 종료 후 화면에서 숨김
                certificationViewModel.timerStop()
                binding.join1TxtTimer.visibility = View.GONE

                // 인증 상태 초기화
                viewModel.emailVerifyResponse.value = null
                certificationViewModel.mailCertificationNumberCheck = false
                binding.join1EditCertificationNumber.text?.clear()
                binding.join1BtnCertificationConfirmation.isEnabled = true
                binding.join1TxtVerificationCertification.visibility = View.INVISIBLE
            }

            viewModel.email = text.toString() + "@gnu.ac.kr"

            if (!text.isNullOrEmpty()) {
                binding.join1BtnVerify.setBackgroundResource(R.drawable.background_radius_10dp_solid_main)
                binding.join1BtnVerify.isEnabled = true
            } else {
                binding.join1BtnVerify.setBackgroundResource(R.drawable.background_radius_10dp_solid_gray7)
                binding.join1BtnVerify.isEnabled = false
            }

            checkButtonActiveCondition()

        }

        binding.join1EditCertificationNumber.doOnTextChanged { text, start, count, after ->
            if (!text.isNullOrEmpty()) {
                binding.join1BtnCertificationConfirmation.setBackgroundResource(R.drawable.background_radius_10dp_solid_main)
                binding.join1BtnCertificationConfirmation.isEnabled = true
            } else {
                binding.join1BtnCertificationConfirmation.setBackgroundResource(R.drawable.background_radius_10dp_solid_gray7)
                binding.join1BtnCertificationConfirmation.isEnabled = false
            }
        }

        binding.join1EditPassword.doOnTextChanged { text, start, count, after ->
            binding.join1TxtVerificationPassword.visibility = View.INVISIBLE
            binding.join1TxtVerificationPasswordCheck.visibility = View.INVISIBLE
            checkButtonActiveCondition()
        }

        binding.join1EditPasswordCheck.doOnTextChanged { text, start, count, after ->
            binding.join1TxtVerificationPassword.visibility = View.INVISIBLE
            binding.join1TxtVerificationPasswordCheck.visibility = View.INVISIBLE
            checkButtonActiveCondition()
        }

        binding.join1ImgBack.setOnClickListener { findNavController().popBackStack() }

        /* 다음 버튼
        * 회원가입에 필요한 뷰모델 필수 정보, 뷰에 표시된 정보가 일치해야 하며, 누락된 것이 없어야 함
        * */
        binding.join1BtnNext.setOnClickListener {

            if (certificationViewModel.mailCertificationNumberCheck && viewModel.email != null) {
                if (binding.join1EditPassword.text.toString()
                        .isEmpty() || binding.join1EditPasswordCheck.text.toString().isEmpty()
                ) {
                    binding.join1TxtVerificationPassword.text = "비빌번호 입력이 완료되지 않았습니다."
                    binding.join1TxtVerificationPassword.setTextColor(
                        resources.getColor(
                            R.color.main, null
                        )
                    )
                    binding.join1TxtVerificationPassword.visibility = View.VISIBLE
                } else {
                    if (binding.join1EditPassword.text.toString() == binding.join1EditPasswordCheck.text.toString()) {
                        if (validatePassword(binding.join1EditPassword.text.toString())) {
                            viewModel.password = binding.join1EditPassword.text.toString()
                            findNavController().navigate(R.id.action_join1Fragment_to_join2Fragment)
                        } else {
                            binding.join1TxtVerificationPassword.text = "비밀번호가 유효하지 않습니다."
                            binding.join1TxtVerificationPassword.setTextColor(
                                resources.getColor(
                                    R.color.main, null
                                )
                            )
                            binding.join1TxtVerificationPassword.visibility = View.VISIBLE
                        }
                    } else {
                        binding.join1TxtVerificationPasswordCheck.text = "비밀번호가 일치하지 않습니다."
                        binding.join1TxtVerificationPasswordCheck.setTextColor(
                            resources.getColor(
                                R.color.main, null
                            )
                        )
                        binding.join1TxtVerificationPasswordCheck.visibility = View.VISIBLE
                    }
                }
            } else {
                binding.join1TxtVerificationCertification.text = "인증이 완료되지 않았습니다."
                binding.join1TxtVerificationCertification.setTextColor(
                    resources.getColor(
                        R.color.main, null
                    )
                )
                binding.join1TxtVerificationCertification.visibility = View.VISIBLE
            }
        }

        binding.join1EditPassword.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                if (validatePassword(binding.join1EditPassword.text.toString())) {
                    binding.join1TxtVerificationPassword.visibility = View.INVISIBLE
                    binding.join1EditPasswordCheck.requestFocus()
                } else {
                    binding.join1TxtVerificationPassword.text = "비밀번호가 유효하지 않습니다."
                    binding.join1TxtVerificationPassword.setTextColor(
                        resources.getColor(
                            R.color.main, null
                        )
                    )
                    binding.join1TxtVerificationPassword.visibility = View.VISIBLE
                }
            }
            true
        }

        binding.join1EditPasswordCheck.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (binding.join1EditPassword.text.toString() == binding.join1EditPasswordCheck.text.toString()) {
                    binding.join1TxtVerificationPasswordCheck.visibility = View.INVISIBLE
                    binding.root.hideSoftKeyboard()
                } else {
                    binding.join1TxtVerificationPasswordCheck.text = "비밀번호가 일치하지 않습니다."
                    binding.join1TxtVerificationPasswordCheck.setTextColor(
                        resources.getColor(
                            R.color.main, null
                        )
                    )
                    binding.join1TxtVerificationPasswordCheck.visibility = View.VISIBLE
                }
            }
            true
        }
    }

    private fun setObserver() {
        /*
        * 이메일 인증번호를 전송했을 때 타이머를 시작함
        * */
        viewModel.mailCertificationNumber.eventObserve(viewLifecycleOwner) {
            certificationViewModel.timerStart()
            certificationViewModel.mailCertificationNumberCheck = false
            viewModel.emailVerifyResponse.value = null
            binding.join1BtnCertificationConfirmation.isEnabled = true
            binding.join1TxtVerificationCertification.visibility = View.INVISIBLE
            binding.join1TxtTimer.visibility = View.VISIBLE
            checkButtonActiveCondition()
        }

        /*
        * 인증 완료, 실패된 상태를 표시
        * */
        viewModel.emailVerifyResponse.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess == true) {
                binding.join1TxtVerificationCertification.text = "인증이 완료되었습니다."
                binding.join1TxtVerificationCertification.setTextColor(
                    resources.getColor(
                        R.color.secondary, null
                    )
                )

                binding.join1TxtVerificationCertification.visibility = View.VISIBLE
                certificationViewModel.mailCertificationNumberCheck = true
                certificationViewModel.timerStop()

                binding.join1TxtTimer.visibility = View.GONE
                binding.join1BtnCertificationConfirmation.isEnabled = false
            } else if (isSuccess == false) {
                binding.join1TxtVerificationCertification.text = "인증번호가 일치하지 않습니다."
                binding.join1TxtVerificationCertification.setTextColor(
                    resources.getColor(
                        R.color.main, null
                    )
                )

                binding.join1TxtVerificationCertification.visibility = View.VISIBLE
                binding.join1BtnCertificationConfirmation.isEnabled = true
                certificationViewModel.mailCertificationNumberCheck = false
            } else {
                binding.join1TxtVerificationCertification.visibility = View.INVISIBLE
                binding.join1TxtTimer.visibility = View.GONE
                binding.join1BtnCertificationConfirmation.isEnabled = true
            }

            checkButtonActiveCondition()
        }

        certificationViewModel.timerCount.observe(viewLifecycleOwner) {
            val formattedTime = convertMillisecondsToTime(it)
            binding.join1TxtTimer.text = formattedTime

            if (it == 0L) {
                binding.join1TxtTimer.visibility = View.GONE
                certificationViewModel.timerStop()
            }
        }

        buttonActiveCheckViewModel.buttonActiveCheck.observe(viewLifecycleOwner) {
            if (it) {
                binding.join1BtnNext.setBackgroundResource(R.drawable.background_radius_10dp_solid_main)
                binding.join1BtnNext.isEnabled = true
            } else {
                binding.join1BtnNext.setBackgroundResource(R.drawable.background_radius_10dp_solid_gray7)
                binding.join1BtnNext.isEnabled = false
            }
        }
    }

    private fun checkButtonActiveCondition() {
        buttonActiveCheckViewModel.buttonActiveCheck.value =
            binding.join1EditPassword.text.toString() == binding.join1EditPasswordCheck.text.toString() && validatePassword(
                binding.join1EditPassword.text.toString()
            ) && certificationViewModel.mailCertificationNumberCheck && !binding.join1EditPassword.text.isNullOrBlank() && !binding.join1EditPasswordCheck.text.isNullOrBlank() && !binding.join1EditEmail.text.isNullOrBlank()
    }

    private fun validatePassword(password: String): Boolean {
        val passwordRegex = Regex("(?=.*[A-Za-z])(?=.*[!@#$%^&*()-+_])(?=\\S+\$).{8,}")
        return passwordRegex.matches(password)
    }
}