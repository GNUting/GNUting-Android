package com.changs.android.gnuting_android.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.changs.android.gnuting_android.data.model.MemoResult
import com.changs.android.gnuting_android.databinding.MemoListItemBinding

class MemoListPagingAdapter(private val onClick: (MemoResult) -> Unit) :
    PagingDataAdapter<MemoResult, MemoListPagingAdapter.PagingViewHolder>(object :
        DiffUtil.ItemCallback<MemoResult>() {

        override fun areItemsTheSame(oldItem: MemoResult, newItem: MemoResult): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MemoResult, newItem: MemoResult): Boolean {
            return oldItem == newItem
        }
    }) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagingViewHolder {
        val binding =
            MemoListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        val displayMetrics = parent.context.resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels

        // RecyclerView의 양쪽 마진 (27dp * 2)
        val marginHorizontal = 54 * displayMetrics.density

        // 컬럼 간의 패딩 (20dp)
        val columnPadding = 20 * displayMetrics.density

        // 두 개의 아이템으로 나눈 후, 가운데 패딩을 고려해 크기를 조정
        val itemWidth = ((screenWidth - marginHorizontal - columnPadding) / 2).toInt()

        // 가로, 세로 동일한 크기로 설정
        val params = binding.root.layoutParams
        params.width = itemWidth
        params.height = itemWidth

        binding.root.layoutParams = params

        return PagingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PagingViewHolder, position: Int) {
        val item = getItem(position)
        item?.let { holder.bind(it) }
    }

    inner class PagingViewHolder(private val binding: MemoListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MemoResult) = with(binding) {
            root.setOnClickListener {
                onClick(item)
            }

            memoTxt.text = item.content
        }
    }
}

