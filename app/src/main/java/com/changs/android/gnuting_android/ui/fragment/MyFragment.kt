package com.changs.android.gnuting_android.ui.fragment

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.util.Util
import com.changs.android.gnuting_android.GNUApplication
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.data.model.Member
import com.changs.android.gnuting_android.databinding.FragmentJoin1Binding
import com.changs.android.gnuting_android.databinding.FragmentMyBinding
import com.changs.android.gnuting_android.ui.HomeActivity
import com.changs.android.gnuting_android.ui.MainActivity
import com.changs.android.gnuting_android.util.Constant
import com.changs.android.gnuting_android.util.eventObserve
import com.changs.android.gnuting_android.util.showTwoButtonDialog
import com.changs.android.gnuting_android.viewmodel.HomeMainViewModel
import com.changs.android.gnuting_android.viewmodel.MainViewModel
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.firebase.installations.Utils
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class MyFragment : BaseFragment<FragmentMyBinding>(FragmentMyBinding::bind, R.layout.fragment_my) {
    private val viewModel: HomeMainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.myInfo.value?.let { myInfo ->
            binding.myTxtName.text = myInfo.nickname
            binding.myTxtInfo.text = "${myInfo.department} | ${myInfo.age} | ${myInfo.studentId}"
            binding.myTxtIntro.text = myInfo.userSelfIntroduction

            Glide.with(this@MyFragment).load(myInfo.profileImage).error(R.drawable.ic_profile)
                .into(binding.myImgProfile)

            binding.myTxtEditProfile.setOnClickListener {
                val action = MyFragmentDirections.actionMyFragmentToEditProfileFragment(myInfo)
                findNavController().navigate(action)
            }
        }

        setObserver()
        setListener()

    }


    private fun setListener() {
        binding.myTxtMenuOpenSource.setOnClickListener {
            startActivity(Intent(requireContext(), OssLicensesMenuActivity::class.java))
            OssLicensesMenuActivity.setActivityTitle("오픈소스 라이선스")
        }

        binding.myTxtMenuLogout.setOnClickListener {
            showTwoButtonDialog(
                context = requireContext(), titleText = "로그아웃 하시겠습니까?", rightButtonText = "로그아웃"
            ) {
                viewModel.logoutUser()
            }
        }

        binding.myTxtMenuWithdrawal.setOnClickListener {
            showTwoButtonDialog(
                context = requireContext(), titleText = "탈퇴 하시겠습니까?", rightButtonText = "탈퇴"
            ) {
                viewModel.withdrawal()
            }
        }

        binding.myTxtMenuMyPosts.setOnClickListener {
            findNavController().navigate(R.id.action_myFragment_to_myPostListFragment)
        }
    }

    private fun setObserver() {
        viewModel.myInfo.observe(viewLifecycleOwner) {
            it?.let { myInfo ->
                binding.myTxtName.text = myInfo.nickname
                binding.myTxtInfo.text =
                    "${myInfo.department} | ${myInfo.age} | ${myInfo.studentId}"
                binding.myTxtIntro.text = myInfo.userSelfIntroduction

                Glide.with(this@MyFragment).load(myInfo.profileImage).error(R.drawable.ic_profile)
                    .into(binding.myImgProfile)

                binding.myTxtEditProfile.setOnClickListener {
                    val action = MyFragmentDirections.actionMyFragmentToEditProfileFragment(myInfo)
                    findNavController().navigate(action)
                }
            }
        }

        viewModel.logoutResponse.eventObserve(viewLifecycleOwner) {
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        viewModel.withdrawalResponse.eventObserve(viewLifecycleOwner) {
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}