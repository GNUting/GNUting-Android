package com.changs.android.gnuting_android.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.changs.android.gnuting_android.GNUApplication
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.data.model.Member
import com.changs.android.gnuting_android.databinding.FragmentJoin1Binding
import com.changs.android.gnuting_android.databinding.FragmentMyBinding
import com.changs.android.gnuting_android.ui.HomeActivity
import com.changs.android.gnuting_android.ui.MainActivity
import com.changs.android.gnuting_android.util.Constant
import com.changs.android.gnuting_android.viewmodel.HomeMainViewModel
import com.changs.android.gnuting_android.viewmodel.MainViewModel
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

            binding.myTxtEditProfile.setOnClickListener {
                val action = MyFragmentDirections.actionMyFragmentToEditProfileFragment(myInfo)
                findNavController().navigate(action)
            }
        }

        binding.myTxtMenuOpenSource.setOnClickListener {
            startActivity(Intent(requireContext(), OssLicensesMenuActivity::class.java))
            OssLicensesMenuActivity.setActivityTitle("오픈소스 라이선스")
        }

        binding.myTxtMenuReport.setOnClickListener {
            // TODO: 마이페이지에서 신고 API 개발되면 신고하기 페이지로 이동하는 navigate 추가 예정
        }

        binding.myTxtMenuLogout.setOnClickListener {
            GNUApplication.sharedPreferences.edit().putString(Constant.X_ACCESS_TOKEN, null).apply()
            viewModel.logoutUser()
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

        }

        binding.myTxtMenuMyPosts.setOnClickListener {
            findNavController().navigate(R.id.action_myFragment_to_myPostListFragment)
        }

    }
}