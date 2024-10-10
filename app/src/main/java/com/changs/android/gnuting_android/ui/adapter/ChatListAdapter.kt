package com.changs.android.gnuting_android.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.data.model.ChatListResult
import com.changs.android.gnuting_android.data.model.ChatRoomUser
import com.changs.android.gnuting_android.databinding.ChatItem2Binding
import com.changs.android.gnuting_android.util.convertToKoreanTime


class ChatListAdapter(private val listener: (Int, String, String, List<ChatRoomUser?>) -> Unit) :
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
        LayoutInflater.from(parent.context).inflate(R.layout.chat_item2, parent, false)
    ) {
        private val binding = ChatItem2Binding.bind(itemView)

        fun bind(
            item: ChatListResult, listener: (Int, String, String, List<ChatRoomUser?>) -> Unit
        ) {
            binding.chatItemImgNew.isVisible = item.hasNewMessage

            val userNameList = item.chatRoomUsers.filterNotNull().map { it.nickname }.joinToString(separator = ", ")
            val user = item.chatRoomUsers.filterNotNull().firstOrNull()
            val info = if (user != null) "${user.studentId} | ${user.department}" else ""

            binding.chatItemTxtType.text = item.title
            binding.chatItemTxtInfo.text = info
            binding.chatItemTxtMessage.text = item.lastMessage
            binding.chatItemTxtTime.text = convertToKoreanTime(item.lastMessageTime)

            if (item.title == "1:1" || item.title == "메모팅") {
                binding.chatItemTxtName.text = user?.nickname ?: "(알 수 없음)"
                binding.chatItemTxtInfo.visibility = View.VISIBLE
            } else {
                binding.chatItemTxtName.text = if (userNameList.isEmpty()) "(알 수 없음)" else userNameList
                binding.chatItemTxtInfo.visibility = View.GONE
            }

            binding.root.setOnClickListener {
                listener(item.id, item.title, info, item.chatRoomUsers)
            }


            Glide.with(binding.root.context).load(user?.profileImage ?: "")
                .error(R.drawable.ic_profile).into(binding.chatItemImgProfile)
        }
    }
}