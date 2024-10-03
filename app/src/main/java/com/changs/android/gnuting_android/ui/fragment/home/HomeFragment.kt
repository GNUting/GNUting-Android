package com.changs.android.gnuting_android.ui.fragment.home

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface.BOLD
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.data.model.PostEventRequestBody
import com.changs.android.gnuting_android.data.model.PostMemoRequestBody
import com.changs.android.gnuting_android.databinding.FragmentHomeBinding
import com.changs.android.gnuting_android.ui.HomeActivity
import com.changs.android.gnuting_android.ui.adapter.ViewPagerAdapter
import com.changs.android.gnuting_android.util.changeStatusBarColor
import com.changs.android.gnuting_android.util.eventObserve
import com.changs.android.gnuting_android.viewmodel.AlarmViewModel
import com.changs.android.gnuting_android.viewmodel.EventViewModel
import com.changs.android.gnuting_android.viewmodel.HomeMainViewModel
import com.changs.android.gnuting_android.viewmodel.MemoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class HomeFragment :
    BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::bind, R.layout.fragment_home) {
    private val viewModel: HomeMainViewModel by activityViewModels()
    private val alarmViewModel: AlarmViewModel by viewModels()
    private val memoViewModel: MemoViewModel by viewModels()
    private val eventViewModel: EventViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        alarmViewModel.getNewAlarm()
        eventViewModel.getEventCheck()
        requireActivity().changeStatusBarColor(R.color.pink)
        setListener()
        setObserver()
        // setPager()
    }

    private fun setListener() {
        binding.homeImgSearch.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_postSearchFragment)
        }

        binding.homeBtnPost.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_post_graph)
        }

        binding.homeCardPostList.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_postListFragment)
        }

        binding.homeCardMyPostList.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_myPostListFragment)
        }

        binding.homeImgAlarm.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_alarmListFragment)
        }

        binding.homeCardMemoting.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_memoFragment)
        }

        binding.homeImgProfile.setOnClickListener {
            (requireActivity() as HomeActivity).selectedItemId(R.id.myFragment)
        }

        binding.homeCardMatching.setOnClickListener {
            (requireActivity() as HomeActivity).showToast("곧 출시 예정이에요.")
        }

        binding.homeBtnMemoPost.setOnClickListener {
            val action = fun(request: PostMemoRequestBody) {
                memoViewModel.postSaveMemo(request)
            }

            showMemoPostDialog(action)
        }
    }

    private fun setPager() {
        binding.homePager.adapter = ViewPagerAdapter(viewModel.images)
        binding.dotsIndicator.attachTo(binding.homePager)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                while (isActive) {
                    if (binding.homePager.currentItem == viewModel.images.size - 1) {
                        viewModel.currentItem = 0
                    }
                    binding.homePager.currentItem = viewModel.currentItem++
                    delay(5000)
                }
            }
        }
    }

    private fun setObserver() {
        eventViewModel.postEventResponse.eventObserve(viewLifecycleOwner) {
            val bundle = bundleOf("id" to it.result.chatId)
            findNavController().navigate(R.id.chatFragment, bundle)
        }

        eventViewModel.eventCheckResponse.observe(viewLifecycleOwner) {
            it?.let {
                if (it == "OPEN") {
                    binding.homeImgBanner.setOnClickListener {
                        val action = fun(request: PostEventRequestBody) {
                            eventViewModel.postEvent(request)
                        }

                        showEventPostDialog(action)
                    }
                } else {
                    binding.homeImgBanner.setOnClickListener {
                        (requireActivity() as HomeActivity).showToast("이벤트 기간이 아닙니다.")
                    }
                }
            }
        }
        viewModel.myInfo.observe(viewLifecycleOwner) {
            it?.let {
                val stringBuilder = SpannableStringBuilder("${it.nickname} 님 안녕하세요 :)")
                stringBuilder.setSpan(StyleSpan(BOLD), 0, it.nickname.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                binding.homeTxtName.text = stringBuilder
                Glide.with(binding.root).load(it.profileImage)
                    .error(R.drawable.ic_profile)
                    .into(binding.homeImgProfile)
            }
        }

        alarmViewModel.newAlarmResponse.observe(viewLifecycleOwner) {
            binding.homeImgNewAlarm.isVisible = it.result
        }

        memoViewModel.toast.eventObserve(viewLifecycleOwner) { text ->
            text?.let {
                (requireActivity() as HomeActivity).showToast(it)
            }
        }

        eventViewModel.toast.eventObserve(viewLifecycleOwner) { text ->
            text?.let {
                (requireActivity() as HomeActivity).showToast(it)
            }
        }
    }

    private fun showMemoPostDialog(action: (PostMemoRequestBody) -> Unit) {
        val dlg = Dialog(requireContext())
        dlg.setCancelable(false)
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg.setContentView(R.layout.dialog_memo_post)
        dlg.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dlg.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val edit = dlg.findViewById<View>(R.id.dialog_memo_post_edit) as AppCompatEditText
        val cancel = dlg.findViewById<View>(R.id.dialog_memo_post_txt_left) as TextView
        val ok = dlg.findViewById<View>(R.id.dialog_memo_post_txt_right) as TextView

        cancel.setOnClickListener { dlg.dismiss() }

        ok.setOnClickListener {
            val content = edit.text.toString()
            val request = PostMemoRequestBody(content = content)
            action(request)
            dlg.dismiss()
        }

        dlg.show()
    }

    private fun showEventPostDialog(action: (PostEventRequestBody) -> Unit) {
        val dlg = Dialog(requireContext())
        dlg.setCancelable(false)
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg.setContentView(R.layout.dialog_event_post)
        dlg.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dlg.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val edit = dlg.findViewById<View>(R.id.dialog_event_post_edit) as AppCompatEditText
        val cancel = dlg.findViewById<View>(R.id.dialog_event_post_txt_left) as TextView
        val ok = dlg.findViewById<View>(R.id.dialog_event_post_txt_right) as TextView

        cancel.setOnClickListener { dlg.dismiss() }

        ok.setOnClickListener {
            val nickname = edit.text.toString()
            val request = PostEventRequestBody(nickname = nickname)
            action(request)
            dlg.dismiss()
        }

        dlg.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().changeStatusBarColor(R.color.white)
    }

}