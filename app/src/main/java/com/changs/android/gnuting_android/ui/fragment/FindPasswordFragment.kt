package com.changs.android.gnuting_android.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doBeforeTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.databinding.FragmentFindPasswordBinding
import com.changs.android.gnuting_android.ui.MainActivity
import com.changs.android.gnuting_android.util.convertMillisecondsToTime
import com.changs.android.gnuting_android.util.eventObserve
import com.changs.android.gnuting_android.viewmodel.CertificationViewModel
import com.changs.android.gnuting_android.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar


class FindPasswordFragment : BaseFragment<FragmentFindPasswordBinding>(FragmentFindPasswordBinding::bind, R.layout.fragment_find_password) {
    private val viewModel: MainViewModel by activityViewModels()
    private val certificationViewModel: CertificationViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
        setListener()

    }

    private fun setListener() {
        binding.findPasswordBtnCertificationConfirmation.setOnClickListener {
            if (certificationViewModel.customTimerDuration.value == 0L) {
                Snackbar.make(binding.root, "인증 시간이 초과되었습니다.", Snackbar.LENGTH_SHORT).show()
            } else {
                val certificationNumber = binding.findPasswordEditCertificationNumber.text.toString()
                viewModel.postEmailVerify(certificationNumber)
            }
        }

        binding.findPasswordBtnNext.setOnClickListener {
            if (certificationViewModel.mailCertificationNumberCheck) {
                if (binding.findPasswordEditPassword.text.toString()
                        .isEmpty() || binding.findPasswordEditPasswordCheck.text.toString().isEmpty()
                ) {
                    Snackbar.make(binding.root, "비빌번호 입력이 완료되지 않았습니다.", Snackbar.LENGTH_SHORT)
                        .show()
                } else {
                    if (binding.findPasswordEditPassword.text.toString() == binding.findPasswordEditPasswordCheck.text.toString()) {
                        if (validatePassword(binding.findPasswordEditPassword.text.toString())) {
                            viewModel.password = binding.findPasswordEditPassword.text.toString()
                            viewModel.patchPassword()
                        } else {
                            Snackbar.make(binding.root, "비밀번호가 유효하지 않습니다.", Snackbar.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        binding.findPasswordTxtVerificationPasswordCheck.text = "비밀번호가 일치하지 않습니다."
                        binding.findPasswordTxtVerificationPasswordCheck.visibility = View.VISIBLE
                    }
                }
            } else {
                Snackbar.make(binding.root, "이메일 인증이 완료되지 않았습니다.", Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.findPasswordBtnVerify.setOnClickListener {
            binding.findPasswordTxtVerificationCertification.visibility = View.INVISIBLE
            viewModel.postMailCertification()
        }

        binding.findPasswordEditEmail.doAfterTextChanged {
            viewModel.email = it.toString() + "@gnu.ac.kr"
        }

        binding.findPasswordEditPassword.doBeforeTextChanged { text, start, count, after ->
            binding.findPasswordTxtVerificationPasswordCheck.visibility = View.INVISIBLE
        }

        binding.findPasswordEditPasswordCheck.doBeforeTextChanged { text, start, count, after ->
            binding.findPasswordTxtVerificationPasswordCheck.visibility = View.INVISIBLE
        }

        binding.findPasswordImgBack.setOnClickListener { findNavController().popBackStack() }
    }

    private fun setObserver() {
        viewModel.passwordResponse.eventObserve(viewLifecycleOwner) {
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        viewModel.mailCertificationNumber.eventObserve(viewLifecycleOwner) {
            certificationViewModel.timerJob.start()
            binding.findPasswordTxtTimer.visibility = View.VISIBLE
        }

        viewModel.emailVerifyResponse.eventObserve(viewLifecycleOwner) {
            binding.findPasswordTxtVerificationCertification.visibility = View.INVISIBLE
            certificationViewModel.mailCertificationNumberCheck = true

            certificationViewModel.timerJob.cancel()
            binding.findPasswordTxtTimer.visibility = View.INVISIBLE
        }

        certificationViewModel.customTimerDuration.observe(viewLifecycleOwner) {
            val formattedTime = convertMillisecondsToTime(it)
            binding.findPasswordTxtTimer.text = formattedTime

            if (it == 0L) {
                binding.findPasswordTxtTimer.visibility = View.INVISIBLE
            }
        }
    }

    private fun validatePassword(password: String): Boolean {
        val passwordRegex = Regex("(?=.*[A-Za-z])(?=.*[!@#$%^&*()-+_])(?=\\S+\$).{8,}")
        return passwordRegex.matches(password)
    }
}