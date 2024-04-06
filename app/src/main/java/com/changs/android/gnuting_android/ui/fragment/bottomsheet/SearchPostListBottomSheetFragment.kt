package com.changs.android.gnuting_android.ui.fragment.bottomsheet

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.databinding.SearchPostListBottomSheetBinding
import com.changs.android.gnuting_android.ui.HomeActivity
import com.changs.android.gnuting_android.ui.adapter.PostSearchListPagingAdapter
import com.changs.android.gnuting_android.util.PostItemNavigator
import com.changs.android.gnuting_android.util.eventObserve
import com.changs.android.gnuting_android.viewmodel.PostViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@OptIn(ExperimentalCoroutinesApi::class)
@AndroidEntryPoint
class SearchPostListBottomSheetFragment : BottomSheetDialogFragment(), PostItemNavigator {
    private var _binding: SearchPostListBottomSheetBinding? = null
    private val postViewModel: PostViewModel by viewModels()
    private lateinit var adapter: PostSearchListPagingAdapter
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setOnShowListener {

            val bottomSheetDialog = it as BottomSheetDialog
            bottomSheetDialog.behavior.skipCollapsed = true

            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

            parentLayout?.let {
                val behaviour = BottomSheetBehavior.from(it)
                setupFullHeight(it)
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return dialog
    }

    override fun getTheme(): Int = R.style.NoMarginsDialog


    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = SearchPostListBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecyclerView()
        setListener()
        setObserver()
    }

    private fun setListener() {
        binding.searchPostListBottomSheetEditSearch.doOnTextChanged { _, _, _, _ ->
            viewLifecycleOwner.lifecycleScope.launch {
                postViewModel.getSearchPostPagingList(binding.searchPostListBottomSheetEditSearch.text.toString())
                    .collectLatest {
                        adapter.submitData(it)
                    }
            }
        }

        binding.searchPostListBottomSheetImgBack.setOnClickListener {
            dismiss()
        }

        binding.searchPostListBottomSheetTxtCancel.setOnClickListener {
            binding.searchPostListBottomSheetEditSearch.text?.clear()
        }
    }

    private fun setObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            postViewModel.getSearchPostPagingList("").collectLatest {
                adapter.submitData(it)
            }
        }

        postViewModel.spinner.observe(viewLifecycleOwner) { show ->
            binding.spinner.visibility = if (show) View.VISIBLE else View.GONE
        }

        postViewModel.toast.eventObserve(viewLifecycleOwner) { text ->
            text?.let {
                (requireActivity() as HomeActivity).showToast(it)
            }
        }

    }

    private fun setRecyclerView() {
        adapter = PostSearchListPagingAdapter(this)
        adapter.addLoadStateListener {
            when (it.refresh) {
                is LoadState.Loading -> binding.spinner.isVisible = true
                is LoadState.NotLoading -> binding.spinner.isVisible = false
                is LoadState.Error -> (requireActivity() as HomeActivity).showToast("네트워크 에러가 발생했습니다.")
            }
        }
        binding.searchPostListBottomSheetRecyclerview.adapter = adapter
    }

    override fun navigateToDetail(id: Int) {
        val bundle = bundleOf("id" to id)
        findNavController().navigate(R.id.action_global_detailFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}