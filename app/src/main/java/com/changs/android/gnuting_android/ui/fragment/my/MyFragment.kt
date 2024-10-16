package com.changs.android.gnuting_android.ui.fragment.my

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
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import timber.log.Timber


@OptIn(ExperimentalCoroutinesApi::class)
@AndroidEntryPoint
class MyFragment : BaseFragment<FragmentMyBinding>(FragmentMyBinding::bind, R.layout.fragment_my) {
    private val viewModel: HomeMainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.myInfo.value?.let { myInfo ->
            binding.myTxtName.text = myInfo.nickname
            binding.myTxtInfo.text = "${myInfo.studentId} | ${myInfo.department}"
            binding.myTxtIntro.text = myInfo.userSelfIntroduction

            Glide.with(this@MyFragment).load(myInfo.profileImage)
                .error(R.drawable.ic_profile)
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
        binding.myTxtMenuAlarmSetting.setOnClickListener {
            findNavController().navigate(R.id.action_myFragment_to_alarmSettingFragment)
        }

        binding.myTxtMenuLegalNotice.setOnClickListener {
            runCatching {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://early-badge-c69.notion.site/d687ee3399a44fbcbc577ee3a73a54e4?pvs=4")
                )
                startActivity(intent)
            }.onFailure {
                Timber.e(it.message ?: "error")
            }
        }

        binding.myTxtMenuTerms.setOnClickListener {
            runCatching {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://equal-kiwi-602.notion.site/9021bea8cf1841fc8a83d26a06c8e72c?pvs=4")
                )
                startActivity(intent)
            }.onFailure {
                Timber.e(it.message ?: "error")
            }
        }

        binding.myTxtMenuOpenSource.setOnClickListener {
            startActivity(Intent(requireContext(), OssLicensesMenuActivity::class.java))
            OssLicensesMenuActivity.setActivityTitle("오픈소스 라이선스")
        }

        binding.myTxtMenuLogout.setOnClickListener {
            showTwoButtonDialog(
                context = requireContext(), titleText = "로그아웃 하시겠습니까?", rightButtonText = "로그아웃"
            ) {
                FirebaseMessaging.getInstance().token.addOnSuccessListener {
                    viewModel.logoutUser(it)
                }.addOnFailureListener {
                    Timber.e(it.message)
                }
            }
        }

        binding.myTxtMenuWithdrawal.setOnClickListener {
            showTwoButtonDialog(
                context = requireContext(), titleText = "탈퇴 하시겠습니까?", rightButtonText = "탈퇴"
            ) {
                viewModel.withdrawal()
            }
        }

        binding.myTxtMenuCustomerServiceCenter.setOnClickListener {
            runCatching {
                val uri = Uri.parse("https://www.instagram.com/gnu_ting/")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                intent.setPackage("com.instagram.android")
                startActivity(intent)
            }.onFailure {
                Timber.e(it.message ?: "error")
                val uri = Uri.parse("https://www.instagram.com/gnu_ting/p/C5bIzh2yIe5/?img_index=1")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
        }
    }

    private fun setObserver() {
        viewModel.myInfo.observe(viewLifecycleOwner) {
            it?.let { myInfo ->
                binding.myTxtName.text = myInfo.nickname
                binding.myTxtInfo.text =
                    "${myInfo.studentId} | ${myInfo.department}"
                binding.myTxtIntro.text = myInfo.userSelfIntroduction

                Glide.with(this@MyFragment)
                    .load(myInfo.profileImage)
                    .error(R.drawable.ic_profile)
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