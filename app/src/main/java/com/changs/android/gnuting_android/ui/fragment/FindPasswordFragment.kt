package com.changs.android.gnuting_android.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doBeforeTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.databinding.FragmentFindPasswordBinding
import com.changs.android.gnuting_android.util.convertMillisecondsToTime
import com.changs.android.gnuting_android.util.eventObserve
import com.changs.android.gnuting_android.viewmodel.Join1ViewModel
import com.changs.android.gnuting_android.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar


class FindPasswordFragment : BaseFragment<FragmentFindPasswordBinding>(FragmentFindPasswordBinding::bind, R.layout.fragment_find_password) {
    private val viewModel: MainViewModel by activityViewModels()
    private val join1ViewModel: Join1ViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
        setListener()

    }

    private fun setListener() {
        binding.findPasswordBtnCertificationConfirmation.setOnClickListener {
            if (join1ViewModel.customTimerDuration.value == 0L) {
                Snackbar.make(binding.root, "인증 시간이 초과되었습니다.", Snackbar.LENGTH_SHORT).show()
            } else {
                val certificationNumber = binding.findPasswordEditCertificationNumber.text.toString()
                viewModel.postEmailVerify(certificationNumber)
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
        viewModel.mailCertificationNumber.eventObserve(viewLifecycleOwner) {
            viewModel.tempMailCertificationNumber = it
            join1ViewModel.timerJob.start()
            binding.findPasswordTxtTimer.visibility = View.VISIBLE
        }

        viewModel.emailVerifyResponse.eventObserve(viewLifecycleOwner) {
            binding.findPasswordTxtVerificationCertification.visibility = View.INVISIBLE
            viewModel.mailCertificationNumberCheck = true

            join1ViewModel.timerJob.cancel()
            binding.findPasswordTxtTimer.visibility = View.INVISIBLE
        }

        join1ViewModel.customTimerDuration.observe(viewLifecycleOwner) {
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