package com.changs.android.gnuting_android.ui.fragment.memo

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.data.model.MemoResult
import com.changs.android.gnuting_android.data.model.PostMemoRequestBody
import com.changs.android.gnuting_android.databinding.FragmentMemoBinding
import com.changs.android.gnuting_android.ui.HomeActivity
import com.changs.android.gnuting_android.ui.adapter.MemoListPagingAdapter
import com.changs.android.gnuting_android.ui.adapter.PostListPagingAdapter
import com.changs.android.gnuting_android.util.eventObserve
import com.changs.android.gnuting_android.util.showTwoButtonDialog
import com.changs.android.gnuting_android.viewmodel.MemoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber


@OptIn(ExperimentalCoroutinesApi::class)
@AndroidEntryPoint
class MemoFragment :
    BaseFragment<FragmentMemoBinding>(FragmentMemoBinding::bind, R.layout.fragment_memo) {
    private val memoViewModel: MemoViewModel by viewModels()
    private lateinit var adapter: MemoListPagingAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecyclerView()
        setObserver()
        setListener()
        memoViewModel.getMemoRemainingCount()
    }

    private fun setObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            memoViewModel.getMemoPagingList().collectLatest {
                adapter.submitData(it)
            }
        }

        memoViewModel.spinner.observe(viewLifecycleOwner) { show ->
            binding.spinner.visibility = if (show) View.VISIBLE else View.GONE
        }

        memoViewModel.toast.eventObserve(viewLifecycleOwner) { text ->
            text?.let {
                (requireActivity() as HomeActivity).showToast(it)
            }
        }

        memoViewModel.remainingCount.observe(viewLifecycleOwner) { count ->
            binding.memoTxtRemainingNumber.text = "${count}회"
        }
    }

    private fun setListener() {
        binding.memoImgBack.setOnClickListener { findNavController().popBackStack() }

        binding.memoImgPostBtn.setOnClickListener {
            val action = fun(request: PostMemoRequestBody) {
                memoViewModel.postSaveMemo(request)
            }

            showMemoPostDialog(action)
        }

        binding.memoRefresh.setColorSchemeColors(resources.getColor(R.color.main, null))
        binding.memoRefresh.setOnRefreshListener {
            viewLifecycleOwner.lifecycleScope.launch {
                memoViewModel.getMemoPagingList().collectLatest {
                    if (binding.memoRefresh.isRefreshing) binding.memoRefresh.isRefreshing = false
                    adapter.submitData(it)
                }

                memoViewModel.getMemoRemainingCount()
            }
        }
    }

    private fun setRecyclerView() {
        adapter = MemoListPagingAdapter { item ->
            val action = fun() {
                memoViewModel.postApplyMemo(item.id)
            }
            showMemoApplyDialog(action)
        }

        adapter.addLoadStateListener {
            if (it.append.endOfPaginationReached) {
                if (adapter.itemCount < 1) {
                    binding.memoLlEmpty.visibility = View.VISIBLE
                } else {
                    binding.memoLlEmpty.visibility = View.GONE
                }
            }

            when (it.refresh) {
                is LoadState.Loading -> binding.spinner.isVisible = true
                is LoadState.NotLoading -> binding.spinner.isVisible = false
                is LoadState.Error ->  (requireActivity() as HomeActivity).showToast("네트워크 에러가 발생했습니다.")
            }
        }

        val spacingInPixels = (20 * resources.displayMetrics.density).toInt()

        binding.memoRecyclerview.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: android.graphics.Rect, view: View,
                parent: RecyclerView, state: RecyclerView.State
            ) {
                outRect.right = spacingInPixels / 2
                outRect.left = spacingInPixels / 2
            }
        })

        binding.memoRecyclerview.adapter = adapter
    }

    private fun showMemoApplyDialog(action: () -> Unit) {
        val dlg = Dialog(requireContext())
        dlg.setCancelable(false)
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg.setContentView(R.layout.dialog_memo_apply)
        dlg.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dlg.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val cancel = dlg.findViewById<View>(R.id.dialog_memo_apply_txt_left) as TextView
        val ok = dlg.findViewById<View>(R.id.dialog_memo_apply_txt_right) as TextView

        cancel.setOnClickListener { dlg.dismiss() }

        ok.setOnClickListener {
            action()
            dlg.dismiss()
        }

        dlg.show()
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

    override fun onPause() {
        super.onPause()
        if (binding.memoRefresh.isRefreshing) binding.memoRefresh.isRefreshing = false
    }
}