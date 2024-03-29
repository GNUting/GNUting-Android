package com.changs.android.gnuting_android.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.data.model.InUser
import com.changs.android.gnuting_android.databinding.FragmentMyBinding
import com.changs.android.gnuting_android.ui.MainActivity
import com.changs.android.gnuting_android.util.eventObserve
import com.changs.android.gnuting_android.util.showTwoButtonDialog
import com.changs.android.gnuting_android.viewmodel.HomeMainViewModel
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
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

            binding.myImgProfile.setOnClickListener {
                val myUserInfo = InUser(
                    age = myInfo.age,
                    department = myInfo.department,
                    gender = myInfo.gender,
                    id = myInfo.id,
                    nickname = myInfo.nickname,
                    profileImage = myInfo.profileImage,
                    studentId = myInfo.studentId,
                    userRole = myInfo.userRole,
                    userSelfIntroduction = myInfo.userSelfIntroduction
                )

                val args = bundleOf("user" to myUserInfo)
                findNavController().navigate(R.id.photoFragment, args)
            }

            binding.myTxtEditProfile.setOnClickListener {
                val action = MyFragmentDirections.actionMyFragmentToEditProfileFragment(myInfo)
                findNavController().navigate(action)
            }
        }

        setObserver()
        setListener()

    }


    private fun setListener() {
        binding.myTxtMenuLegalNotice.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://gnuting.github.io/GNUting-PrivacyPolicy/privacy_policy.pdf")
            )
            startActivity(intent)
        }

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