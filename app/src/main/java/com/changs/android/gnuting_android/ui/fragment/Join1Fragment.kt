package com.changs.android.gnuting_android.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.databinding.FragmentJoin1Binding
import com.changs.android.gnuting_android.util.convertMillisecondsToTime
import com.changs.android.gnuting_android.util.eventObserve
import com.changs.android.gnuting_android.viewmodel.Join1ViewModel
import com.changs.android.gnuting_android.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar

class Join1Fragment :
    BaseFragment<FragmentJoin1Binding>(FragmentJoin1Binding::bind, R.layout.fragment_join1) {
    private val viewModel: MainViewModel by activityViewModels()
    private val join1ViewModel: Join1ViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListener()
        setObserver()
    }

    private fun setListener() {
        binding.join1BtnCertificationConfirmation.setOnClickListener {
            if (join1ViewModel.customTimerDuration.value == 0L) {
                Snackbar.make(binding.root, "인증 시간이 초과되었습니다.", Snackbar.LENGTH_SHORT).show()
            } else {
                val certificationNumber = binding.join1EditCertificationNumber.text.toString()

                if (viewModel.tempMailCertificationNumber == certificationNumber) {
                    binding.join1TxtVerificationCertification.visibility = View.INVISIBLE
                    viewModel.mailCertificationNumberCheck = true
                    join1ViewModel.timerJob.cancel()
                    binding.join1TxtTimer.visibility = View.INVISIBLE
                } else {
                    binding.join1TxtVerificationCertification.text = "인증번호가 일치하지 않습니다."
                    binding.join1TxtVerificationCertification.visibility = View.VISIBLE
                    viewModel.mailCertificationNumberCheck = false
                }
            }

        }

        binding.join1BtnVerify.setOnClickListener {
            binding.join1TxtVerificationCertification.visibility = View.INVISIBLE
            viewModel.postMailCertification()
        }

        binding.join1EditEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.email = s.toString() + "@gnu.ac.kr"
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.join1EditPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                binding.join1TxtVerificationPasswordCheck.visibility = View.INVISIBLE
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.join1EditPasswordCheck.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                binding.join1TxtVerificationPasswordCheck.visibility = View.INVISIBLE
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
            }
        })


        binding.join1ImgBack.setOnClickListener { findNavController().popBackStack() }

        binding.join1BtnNext.setOnClickListener {
            if (viewModel.mailCertificationNumberCheck) {
                if (binding.join1EditPassword.text.toString()
                        .isEmpty() || binding.join1EditPasswordCheck.text.toString().isEmpty()
                ) {
                    Snackbar.make(binding.root, "비빌번호 입력이 완료되지 않았습니다.", Snackbar.LENGTH_SHORT)
                        .show()
                } else {
                    if (binding.join1EditPassword.text.toString() == binding.join1EditPasswordCheck.text.toString()) {
                        viewModel.password = binding.join1EditPassword.text.toString()
                        findNavController().navigate(R.id.action_join1Fragment_to_join2Fragment)
                    } else {
                        binding.join1TxtVerificationPasswordCheck.text = "비밀번호가 일치하지 않습니다."
                        binding.join1TxtVerificationPasswordCheck.visibility = View.VISIBLE
                    }
                }
            } else {
                Snackbar.make(binding.root, "이메일 인증이 완료되지 않았습니다.", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun setObserver() {
        viewModel.mailCertificationNumber.eventObserve(viewLifecycleOwner) {
            viewModel.tempMailCertificationNumber = it
            join1ViewModel.timerJob.start()
            binding.join1TxtTimer.visibility = View.VISIBLE
        }

        join1ViewModel.customTimerDuration.observe(viewLifecycleOwner) {
            val formattedTime = convertMillisecondsToTime(it)
            binding.join1TxtTimer.text = formattedTime

            if (it == 0L) {
                binding.join1TxtTimer.visibility = View.INVISIBLE
            }
        }
    }

}