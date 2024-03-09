package com.changs.android.gnuting_android.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.data.model.Content
import com.changs.android.gnuting_android.data.model.HomePostItem
import com.changs.android.gnuting_android.data.model.PostListItem
import com.changs.android.gnuting_android.data.model.PostResult
import com.changs.android.gnuting_android.databinding.HomeListItemBinding
import com.changs.android.gnuting_android.databinding.PostListItemBinding
import com.changs.android.gnuting_android.util.PostItemNavigator


class PostSearchListAdapter(private val listener: PostItemNavigator) :
    ListAdapter<Content, PostSearchListAdapter.ViewHolder>(object : DiffUtil.ItemCallback<Content>() {
        override fun areItemsTheSame(oldItem: Content, newItem: Content): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Content, newItem: Content): Boolean {
            return oldItem.boardId == newItem.boardId
        }
    }) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostSearchListAdapter.ViewHolder {
        return PostSearchListAdapter.ViewHolder(parent, listener)
    }

    override fun onBindViewHolder(holder: PostSearchListAdapter.ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    class ViewHolder(parent: ViewGroup, private val listener: PostItemNavigator) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.post_list_item, parent, false)
    ) {
        private val binding = PostListItemBinding.bind(itemView)

        fun bind(item: Content) {
            binding.postListItemTxtTitle.text = item.title
            binding.postListItemTxtInfo.text = "${item.department} | ${item.studentId}"

            binding.root.setOnClickListener {
                listener.navigateToDetail(item.boardId)
            }
        }
    }
}