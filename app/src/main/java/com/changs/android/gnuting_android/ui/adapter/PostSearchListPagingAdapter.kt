package com.changs.android.gnuting_android.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
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
            binding.postListItemTxtTitle.text = item.title
            binding.postListItemTxtInfo.text = "${item.department} | ${item.studentId}"

            binding.root.setOnClickListener {
                listener.navigateToDetail(item.boardId)
            }
        }
    }
}

