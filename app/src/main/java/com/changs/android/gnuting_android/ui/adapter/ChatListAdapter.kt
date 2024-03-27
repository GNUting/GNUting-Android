package com.changs.android.gnuting_android.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.data.model.ChatListResult
import com.changs.android.gnuting_android.databinding.ChatItemBinding


class ChatListAdapter(private val listener: (Int, String, String) -> Unit) :
    ListAdapter<ChatListResult, ChatListAdapter.ViewHolder>(object :
        DiffUtil.ItemCallback<ChatListResult>() {
        override fun areItemsTheSame(oldItem: ChatListResult, newItem: ChatListResult): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ChatListResult, newItem: ChatListResult): Boolean {
            return oldItem == newItem
        }
    }) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position], listener)
    }

    class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.chat_item, parent, false)
    ) {
        private val binding = ChatItemBinding.bind(itemView)

        fun bind(item: ChatListResult, listener: (Int, String, String) -> Unit) {
            // TODO: response 구조 변경될 예정

            val info = "${item.applyLeaderDepartment} | ${"몇학번인지 표시~"}"

            binding.chatItemTxtTitle.text = item.title
            binding.chatItemTxtInfo.text = info

            binding.root.setOnClickListener {
                listener(item.id, item.title, info)
            }

            binding.chatItemTxtLastMessage.text = "마지막 메세지~"

            Glide.with(binding.root.context).load("").error(R.drawable.ic_profile)
                .into(binding.chatItemImgProfile)
        }
    }
}