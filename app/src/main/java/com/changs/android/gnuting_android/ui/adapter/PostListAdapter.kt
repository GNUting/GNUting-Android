package com.changs.android.gnuting_android.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.changs.android.gnuting_android.R
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
                if (item.status == "OPEN") listener.navigateToDetail(item.id)
            }

            binding.postListItemTxtTitle.text = item.title
            binding.postListItemTxtInfo.text = "${item.time} | ${item.user.department} | ${item.user.studentId}"
            binding.postListItemTxtMembers.text = "인원: ${item.inUserCount}명"
        }
    }
}

