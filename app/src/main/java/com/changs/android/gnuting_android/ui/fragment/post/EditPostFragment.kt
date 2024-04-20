package com.changs.android.gnuting_android.ui.fragment.post

import android.os.Bundle
import android.text.InputFilter
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.data.model.InUser
import com.changs.android.gnuting_android.data.model.SaveRequest
import com.changs.android.gnuting_android.databinding.FragmentEditPostBinding
import com.changs.android.gnuting_android.ui.HomeActivity
import com.changs.android.gnuting_android.ui.adapter.PostMemberAdapter
import com.changs.android.gnuting_android.ui.fragment.bottomsheet.SearchMemberBottomSheetFragment
import com.changs.android.gnuting_android.util.eventObserve
import com.changs.android.gnuting_android.viewmodel.HomeMainViewModel
import com.changs.android.gnuting_android.viewmodel.MemberAddViewModel
import com.changs.android.gnuting_android.viewmodel.PostViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
@AndroidEntryPoint
class EditPostFragment : BaseFragment<FragmentEditPostBinding>(
    FragmentEditPostBinding::bind, R.layout.fragment_edit_post
) {
    private val postViewModel: PostViewModel by viewModels()
    private val memberAddViewModel: MemberAddViewModel by hiltNavGraphViewModels(R.id.detail_graph)
    private lateinit var adapter: PostMemberAdapter
    private val args: EditPostFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        args.detail.apply {
            binding.editPostEditTitle.setText(title)
            binding.editPostEditDetail.setText(detail)

            if (memberAddViewModel.currentMember.value == null)
                memberAddViewModel.currentMember.value = inUser.toMutableList()
        }

        setRecyclerView()
        setListener()
        setObserver()
    }

    private fun setListener() {
        binding.editPostEditTitle.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(30))
        binding.editPostEditDetail.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(300))

        binding.editPostImgBack.setOnClickListener { findNavController().popBackStack() }

        binding.editPostLlAddMember.setOnClickListener {
            findNavController().navigate(R.id.action_editPostFragment_to_searchMemberBottomSheetFragment)
        }

        binding.editPostTxtComplete.setOnClickListener {
            if (!binding.editPostEditTitle.text.isNullOrEmpty() && !binding.editPostEditDetail.text.isNullOrEmpty()) {
                if ((memberAddViewModel.currentMember.value?.size ?: 0) != 0) {
                    val request = SaveRequest(
                        detail = binding.editPostEditDetail.text.toString(),
                        title = binding.editPostEditTitle.text.toString(),
                        inUser = memberAddViewModel.currentMember.value!!.toList()
                    )
                    postViewModel.patchSave(args.id, request)
                }
            } else {
                (requireActivity() as HomeActivity).showToast("게시글 작성을 완료해주세요.")
            }
        }
    }

    private fun setRecyclerView() {
        adapter = PostMemberAdapter(::navigateListener)
        binding.editPostRecyclerview.adapter = adapter
    }

    private fun setObserver() {
        postViewModel.spinner.observe(viewLifecycleOwner) { show ->
            binding.spinner.visibility = if (show) View.VISIBLE else View.GONE
        }

        postViewModel.toast.eventObserve(viewLifecycleOwner) { text ->
            text?.let {
                (requireActivity() as HomeActivity).showToast(it)
            }
        }

        memberAddViewModel.currentMember.observe(viewLifecycleOwner) {
            binding.editPostTxtMemberTitle.text = "멤버 (${it.size})"
            adapter.submitList(it)
        }

        postViewModel.patchPostDetailResponse.eventObserve(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
    }

    private fun navigateListener(user: InUser) {
        val args = bundleOf("user" to user)
        findNavController().navigate(R.id.action_editPostFragment_to_photoFragment3, args)
    }
}