package com.changs.android.gnuting_android.ui.fragment.user

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.databinding.FragmentLoginBinding
import com.changs.android.gnuting_android.ui.HomeActivity
import com.changs.android.gnuting_android.util.eventObserve
import com.changs.android.gnuting_android.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
class LoginFragment :
    BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::bind, R.layout.fragment_login) {
    private val viewModel: MainViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListener()
        setObserver()
    }

    private fun setListener() {
        binding.loginBtnLogin.setOnClickListener {
            if (!binding.loginEditEmail.text.isNullOrEmpty() && !binding.loginEditPassword.text.isNullOrEmpty()) {
                viewModel.postLogin(
                    binding.loginEditEmail.text.toString(), binding.loginEditPassword.text.toString()
                )
            } else {
                Toast.makeText(requireContext(), "이메일, 패스워드를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }


        binding.loginTxtFindPassword.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_passwordFragment)
        }

        binding.loginTxtJoin.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_policyFragment)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun setObserver() {
        viewModel.loginResponse.eventObserve(viewLifecycleOwner) {
            if (it.isSuccess == true) {
                val intent = Intent(requireContext(), HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }
}