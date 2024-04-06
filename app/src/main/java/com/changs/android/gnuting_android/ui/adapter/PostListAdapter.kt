package com.changs.android.gnuting_android.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.changs.android.gnuting_android.data.model.PostResult
import com.changs.android.gnuting_android.databinding.PostListItemBinding
import com.changs.android.gnuting_android.util.PostItemNavigator


class PostListAdapter(private val listener: PostItemNavigator) :
    ListAdapter<PostResult, PostListAdapter.ViewHolder>(object :
        DiffUtil.ItemCallback<PostResult>() {

        override fun areItemsTheSame(oldItem: PostResult, newItem: PostResult): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: PostResult, newItem: PostResult): Boolean {
            return oldItem == newItem
        }
    }) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            PostListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        item?.let { holder.bind(it, listener) }
    }

    inner class ViewHolder(private val binding: PostListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PostResult, listener: PostItemNavigator) = with(binding) {

            binding.postListItemTxtTitle.text = item.title
            binding.postListItemTxtInfo.text = "${item.user.department} | ${item.user.studentId}"
            binding.postListItemTxtMember.text = "인원 : ${item.inUserCount}명"
            binding.root.setOnClickListener {
                listener.navigateToDetail(item.id)
            }
        }
    }
}

