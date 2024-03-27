package com.changs.android.gnuting_android.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.data.model.ChatItem
import com.changs.android.gnuting_android.data.model.MessageItem
import com.changs.android.gnuting_android.databinding.ChatItemBinding
import com.changs.android.gnuting_android.databinding.MeChatItemBinding
import com.changs.android.gnuting_android.databinding.OtherChatItemBinding
import com.changs.android.gnuting_android.ui.PhotoActivity


class ChatAdapter(private val myNickName: String) :
    ListAdapter<MessageItem, RecyclerView.ViewHolder>(object :
        DiffUtil.ItemCallback<MessageItem>() {
        override fun areItemsTheSame(oldItem: MessageItem, newItem: MessageItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: MessageItem, newItem: MessageItem): Boolean {
            return oldItem == newItem
        }
    }) {
    private val VIEW_TYPE_USER_MESSAGE_ME = 0
    private val VIEW_TYPE_USER_MESSAGE_OTHER = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_USER_MESSAGE_ME -> {
                MeViewHolder(parent)
            }

            else -> {
                OtherViewHolder(parent)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_TYPE_USER_MESSAGE_ME -> {
                holder as MeViewHolder
                holder.bind(currentList[position])
            }

            else -> {
                holder as OtherViewHolder
                holder.bind(currentList[position])
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position).nickname) {
            myNickName -> {
                VIEW_TYPE_USER_MESSAGE_ME
            }

            else -> {
                VIEW_TYPE_USER_MESSAGE_OTHER
            }
        }
    }


    class MeViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.me_chat_item, parent, false)
    ) {
        private val binding = MeChatItemBinding.bind(itemView)

        fun bind(item: MessageItem) {
            binding.meChatItemTxtMessage.text = item.message
            binding.meChatItemTxtNickname.text = item.nickname

            Glide.with(binding.root.context).load(item.profileImage).error(R.drawable.ic_profile)
                .into(binding.meChatItemImg)

            binding.meChatItemImg.setOnClickListener {
                val intent = Intent(binding.root.context, PhotoActivity::class.java)
                intent.putExtra("img", item.profileImage)
                binding.root.context.startActivity(intent)
            }
        }
    }

    class OtherViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.other_chat_item, parent, false)
    ) {
        private val binding = OtherChatItemBinding.bind(itemView)

        fun bind(item: MessageItem) {
            binding.otherChatItemTxtMessage.text = item.message
            binding.otherChatItemTxtNickname.text = item.nickname

            Glide.with(binding.root.context).load(item.profileImage).error(R.drawable.ic_profile)
                .into(binding.otherChatItemImg)

            binding.otherChatItemImg.setOnClickListener {
                val intent = Intent(binding.root.context, PhotoActivity::class.java)
                intent.putExtra("img", item.profileImage)
                binding.root.context.startActivity(intent)
            }
        }
    }
}
