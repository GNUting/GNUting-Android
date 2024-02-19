package com.changs.android.gnuting_android.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.data.model.HomePostItem
import com.changs.android.gnuting_android.data.model.PostListItem
import com.changs.android.gnuting_android.databinding.HomeListItemBinding
import com.changs.android.gnuting_android.databinding.PostListItemBinding


class PostListAdapter :
    ListAdapter<PostListItem, PostListAdapter.ViewHolder>(object : DiffUtil.ItemCallback<PostListItem>() {
        override fun areItemsTheSame(oldItem: PostListItem, newItem: PostListItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: PostListItem, newItem: PostListItem): Boolean {
            return oldItem.id == newItem.id
        }
    }) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostListAdapter.ViewHolder {
        return PostListAdapter.ViewHolder(parent)
    }

    override fun onBindViewHolder(holder: PostListAdapter.ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.post_list_item, parent, false)
    ) {
        private val binding = PostListItemBinding.bind(itemView)

        fun bind(item: PostListItem) {
            binding.postListItemTxtTitle.text = item.title
            binding.postListItemTxtInfo.text = "${item.department} | ${item.studentId}"
        }
    }
}