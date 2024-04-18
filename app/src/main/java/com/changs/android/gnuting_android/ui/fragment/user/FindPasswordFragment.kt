package com.changs.android.gnuting_android.ui.fragment.user

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.databinding.FragmentFindPasswordBinding
import com.changs.android.gnuting_android.ui.MainActivity
import com.changs.android.gnuting_android.util.convertMillisecondsToTime
import com.changs.android.gnuting_android.util.eventObserve
import com.changs.android.gnuting_android.util.hideSoftKeyboard
import com.changs.android.gnuting_android.viewmodel.ButtonActiveCheckViewModel
import com.changs.android.gnuting_android.viewmodel.CertificationViewModel
import com.changs.android.gnuting_android.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FindPasswordFragment : BaseFragment<FragmentFindPasswordBinding>(
    FragmentFindPasswordBinding::bind, R.layout.fragment_find_password
) {
    private val viewModel: MainViewModel by activityViewModels()
    private val certificationViewModel: CertificationViewModel by viewModels()
    private val buttonActiveCheckViewModel: ButtonActiveCheckViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListener()
        setObserver()
    }

    private fun setListener() {
        binding.findPasswordBtnCertificationConfirmation.setOnClickListener {
            if (certificationViewModel.timerCount.value == 0L) {
                binding.findPasswordTxtVerificationCertification.text = "인증 시간이 초과되었습니다."
                binding.findPasswordTxtVerificationCertification.setTextColor(
                    resources.getColor(
                        R.color.main, null
                    )
                )
                binding.findPasswordTxtVerificationCertification.visibility = View.VISIBLE
            } else {
                val certificationNumber =
                    binding.findPasswordEditCertificationNumber.text.toString()
                viewModel.postEmailVerify(certificationNumber)
            }
        }

        binding.findPasswordBtnNext.setOnClickListener {
            if (certificationViewModel.mailCertificationNumberCheck) {
                if (binding.findPasswordEditPassword.text.toString()
                        .isEmpty() || binding.findPasswordEditPasswordCheck.text.toString()
                        .isEmpty()
                ) {
                    binding.findPasswordTxtVerificationPassword.text = "비빌번호 입력이 완료되지 않았습니다."
                    binding.findPasswordTxtVerificationPassword.setTextColor(
                        resources.getColor(
                            R.color.main, null
                        )
                    )
                    binding.findPasswordTxtVerificationPassword.visibility = View.VISIBLE
                } else {
                    if (binding.findPasswordEditPassword.text.toString() == binding.findPasswordEditPasswordCheck.text.toString()) {
                        if (validatePassword(binding.findPasswordEditPassword.text.toString())) {
                            viewModel.password = binding.findPasswordEditPassword.text.toString()
                            viewModel.patchPassword()
                        } else {
                            binding.findPasswordTxtVerificationPassword.text = "비밀번호가 유효하지 않습니다."
                            binding.findPasswordTxtVerificationPassword.setTextColor(
                                resources.getColor(
                                    R.color.main, null
                                )
                            )
                            binding.findPasswordTxtVerificationPassword.visibility = View.VISIBLE
                        }
                    } else {
                        binding.findPasswordTxtVerificationPasswordCheck.text = "비밀번호가 일치하지 않습니다."
                        binding.findPasswordTxtVerificationPasswordCheck.setTextColor(
                            resources.getColor(
                                R.color.main, null
                            )
                        )
                        binding.findPasswordTxtVerificationPasswordCheck.visibility = View.VISIBLE
                    }
                }
            } else {
                binding.findPasswordTxtVerificationCertification.text = "인증이 완료되지 않았습니다."
                binding.findPasswordTxtVerificationCertification.setTextColor(
                    resources.getColor(
                        R.color.main, null
                    )
                )
                binding.findPasswordTxtVerificationCertification.visibility = View.VISIBLE
            }
        }

        binding.findPasswordBtnVerify.setOnClickListener {
            binding.findPasswordTxtVerificationCertification.visibility = View.INVISIBLE
            viewModel.postMailCertification()
        }

        binding.findPasswordEditEmail.doOnTextChanged { text, start, count, after ->
            viewModel.email = text.toString() + "@gnu.ac.kr"
            if (!text.isNullOrEmpty()) {
                binding.findPasswordBtnVerify.setBackgroundResource(R.drawable.background_radius_10dp_solid_main)
                binding.findPasswordBtnVerify.isEnabled = true
            } else {
                binding.findPasswordBtnVerify.setBackgroundResource(R.drawable.background_radius_10dp_solid_gray7)
                binding.findPasswordBtnVerify.isEnabled = false
            }
        }

        binding.findPasswordEditCertificationNumber.doOnTextChanged { text, start, count, after ->
            if (!text.isNullOrEmpty()) {
                binding.findPasswordBtnCertificationConfirmation.setBackgroundResource(R.drawable.background_radius_10dp_solid_main)
                binding.findPasswordBtnCertificationConfirmation.isEnabled = true
            } else {
                binding.findPasswordBtnCertificationConfirmation.setBackgroundResource(R.drawable.background_radius_10dp_solid_gray7)
                binding.findPasswordBtnCertificationConfirmation.isEnabled = false
            }
        }

        binding.findPasswordEditPassword.doOnTextChanged { text, start, count, after ->
            binding.findPasswordTxtVerificationPassword.visibility = View.INVISIBLE
            binding.findPasswordTxtVerificationPasswordCheck.visibility = View.INVISIBLE
            checkButtonActiveCondition()
        }

        binding.findPasswordEditPasswordCheck.doOnTextChanged { text, start, count, after ->
            binding.findPasswordTxtVerificationPassword.visibility = View.INVISIBLE
            binding.findPasswordTxtVerificationPasswordCheck.visibility = View.INVISIBLE
            checkButtonActiveCondition()
        }

        binding.findPasswordImgBack.setOnClickListener { findNavController().popBackStack() }

        binding.findPasswordEditPassword.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                if (validatePassword(binding.findPasswordEditPassword.text.toString())) {
                    binding.findPasswordTxtVerificationPassword.visibility = View.INVISIBLE
                    binding.findPasswordEditPasswordCheck.requestFocus()
                } else {
                    binding.findPasswordTxtVerificationPassword.text = "비밀번호가 유효하지 않습니다."
                    binding.findPasswordTxtVerificationPassword.setTextColor(
                        resources.getColor(
                            R.color.main, null
                        )
                    )
                    binding.findPasswordTxtVerificationPassword.visibility = View.VISIBLE
                }
            }
            true
        }

        binding.findPasswordEditPasswordCheck.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (binding.findPasswordEditPassword.text.toString() == binding.findPasswordEditPasswordCheck.text.toString()) {
                    binding.findPasswordTxtVerificationPasswordCheck.visibility = View.INVISIBLE
                    binding.root.hideSoftKeyboard()
                } else {
                    binding.findPasswordTxtVerificationPasswordCheck.text = "비밀번호가 일치하지 않습니다."
                    binding.findPasswordTxtVerificationPasswordCheck.setTextColor(
                        resources.getColor(
                            R.color.main, null
                        )
                    )
                    binding.findPasswordTxtVerificationPasswordCheck.visibility = View.VISIBLE
                }
            }
            true
        }
    }

    private fun setObserver() {
        viewModel.passwordResponse.eventObserve(viewLifecycleOwner) {
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        viewModel.mailCertificationNumber.eventObserve(viewLifecycleOwner) {
            certificationViewModel.timerStart()
            certificationViewModel.mailCertificationNumberCheck = false
            binding.findPasswordTxtTimer.visibility = View.VISIBLE
            checkButtonActiveCondition()
        }

        viewModel.emailVerifyResponse.eventObserve(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                binding.findPasswordTxtVerificationCertification.text = "인증이 완료되었습니다."
                binding.findPasswordTxtVerificationCertification.setTextColor(
                    resources.getColor(
                        R.color.secondary, null
                    )
                )

                certificationViewModel.mailCertificationNumberCheck = true
                certificationViewModel.timerStop()

                binding.findPasswordTxtTimer.visibility = View.GONE
            } else {
                binding.findPasswordTxtVerificationCertification.text = "인증번호가 일치하지 않습니다."
                binding.findPasswordTxtVerificationCertification.setTextColor(
                    resources.getColor(
                        R.color.main, null
                    )
                )

                certificationViewModel.mailCertificationNumberCheck = false
            }

            binding.findPasswordTxtVerificationCertification.visibility = View.VISIBLE
            checkButtonActiveCondition()
        }

        certificationViewModel.timerCount.observe(viewLifecycleOwner) {
            val formattedTime = convertMillisecondsToTime(it)
            binding.findPasswordTxtTimer.text = formattedTime

            if (it == 0L) {
                binding.findPasswordTxtTimer.visibility = View.GONE
                certificationViewModel.timerStop()
            }
        }

        buttonActiveCheckViewModel.buttonActiveCheck.observe(viewLifecycleOwner) {
            if (it) {
                binding.findPasswordBtnNext.setBackgroundResource(R.drawable.background_radius_10dp_solid_main)
                binding.findPasswordBtnNext.isEnabled = true
            } else {
                binding.findPasswordBtnNext.setBackgroundResource(R.drawable.background_radius_10dp_solid_gray7)
                binding.findPasswordBtnNext.isEnabled = false
            }
        }
    }

    private fun checkButtonActiveCondition() {
        buttonActiveCheckViewModel.buttonActiveCheck.value =
            binding.findPasswordEditPassword.text.toString() == binding.findPasswordEditPasswordCheck.text.toString() && validatePassword(
                binding.findPasswordEditPassword.text.toString()
            ) && certificationViewModel.mailCertificationNumberCheck && !binding.findPasswordEditPassword.text.isNullOrBlank() && !binding.findPasswordEditPasswordCheck.text.isNullOrBlank() && !binding.findPasswordEditEmail.text.isNullOrBlank()
    }

    private fun validatePassword(password: String): Boolean {
        val passwordRegex = Regex("(?=.*[A-Za-z])(?=.*[!@#$%^&*()-+_])(?=\\S+\$).{8,}")
        return passwordRegex.matches(password)
    }
}