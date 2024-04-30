package com.changs.android.gnuting_android.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.data.model.Content
import com.changs.android.gnuting_android.databinding.PostListItemBinding
import com.changs.android.gnuting_android.util.PostItemNavigator


class PostSearchListPagingAdapter(private val listener: PostItemNavigator) :
    PagingDataAdapter<Content, PostSearchListPagingAdapter.PagingViewHolder>(object :
        DiffUtil.ItemCallback<Content>() {
        override fun areItemsTheSame(oldItem: Content, newItem: Content): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Content, newItem: Content): Boolean {
            return oldItem.boardId == newItem.boardId
        }
    }) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagingViewHolder {
        val binding =
            PostListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PagingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PagingViewHolder, position: Int) {
        val item = getItem(position)
        item?.let { holder.bind(it) }
    }

    inner class PagingViewHolder(private val binding: PostListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Content) {
            if (item.status == "OPEN") {
                binding.postListItemTxtStatus.text = "신청 가능"
                binding.postListItemTxtStatus.setTextColor(binding.root.resources.getColor(R.color.secondary, null))

                binding.postListItemTxtTitle.setTextColor(binding.root.resources.getColor(R.color.black, null))
            } else {
                binding.postListItemTxtStatus.text = "신청 마감"
                binding.postListItemTxtStatus.setTextColor(binding.root.resources.getColor(R.color.main, null))

                binding.postListItemTxtTitle.setTextColor(binding.root.resources.getColor(R.color.gray7, null))
            }

            binding.root.setOnClickListener {
                if (item.status == "OPEN") listener.navigateToDetail(item.boardId)
            }

            binding.postListItemTxtTitle.text = item.title
            binding.postListItemTxtInfo.text = "${item.time} |  ${item.department} | ${item.studentId}"
            binding.postListItemTxtMembers.text = "인원: ${item.inUserCount}명"
        }
    }
}

