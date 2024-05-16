package com.changs.android.gnuting_android.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.data.model.InUser
import com.changs.android.gnuting_android.data.model.MessageItem
import com.changs.android.gnuting_android.databinding.ManagerChatItemBinding
import com.changs.android.gnuting_android.databinding.MeChatItemBinding
import com.changs.android.gnuting_android.databinding.OtherChatItemBinding
import com.changs.android.gnuting_android.util.convertToKoreanTime


class ChatAdapter(private val myNickName: String, private val navigateListener: (InUser) -> Unit) :
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
    private val VIEW_TYPE_USER_MESSAGE_MANAGER = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_USER_MESSAGE_ME -> {
                MeViewHolder(parent, navigateListener)
            }

            VIEW_TYPE_USER_MESSAGE_OTHER -> {
                OtherViewHolder(parent, navigateListener)
            }

            else -> {
                ManagerViewHolder(parent)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_TYPE_USER_MESSAGE_ME -> {
                holder as MeViewHolder
                holder.bind(currentList[position])
            }

            VIEW_TYPE_USER_MESSAGE_OTHER -> {
                holder as OtherViewHolder
                holder.bind(currentList[position])
            }

            else -> {
                holder as ManagerViewHolder
                holder.bind(currentList[position])
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position).messageType) {
            "CHAT" -> {
                if (getItem(position).nickname == myNickName) VIEW_TYPE_USER_MESSAGE_ME
                else VIEW_TYPE_USER_MESSAGE_OTHER
            }

            else -> {
                VIEW_TYPE_USER_MESSAGE_MANAGER
            }
        }
    }


    class MeViewHolder(parent: ViewGroup, private val navigateListener: (InUser) -> Unit) :
        RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.me_chat_item, parent, false)
        ) {
        private val binding = MeChatItemBinding.bind(itemView)

        fun bind(item: MessageItem) {
            binding.meChatItemTxtMessage.text = item.message

            if (!item.createdDate.isNullOrBlank()) {
                binding.meChatItemTxtTime.text = convertToKoreanTime(item.createdDate)
            }
        }
    }

    class OtherViewHolder(parent: ViewGroup, private val navigateListener: (InUser) -> Unit) :
        RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.other_chat_item, parent, false)
        ) {
        private val binding = OtherChatItemBinding.bind(itemView)

        fun bind(item: MessageItem) {
            binding.otherChatItemTxtMessage.text = item.message
            binding.otherChatItemTxtNickname.text = item.nickname ?: "(알 수 없음)"

            if (!item.createdDate.isNullOrBlank()) {
                binding.otherChatItemTxtTime.text = convertToKoreanTime(item.createdDate)
            }

            Glide.with(binding.root.context).load(item.profileImage)
                .error(R.drawable.ic_profile)
                .into(binding.otherChatItemImg)

            if (!item.nickname.isNullOrEmpty() && item.id != null) {
                binding.otherChatItemImg.setOnClickListener {
                    val inUser = InUser(
                        age = "",
                        department = item.department,
                        gender = "",
                        nickname = item.nickname,
                        id = item.id,
                        profileImage = item.profileImage,
                        studentId = item.studentId,
                        userRole = "",
                        userSelfIntroduction = ""
                    )
                    navigateListener(inUser)
                }
            }
        }
    }

    class ManagerViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.manager_chat_item, parent, false)
    ) {
        private val binding = ManagerChatItemBinding.bind(itemView)

        fun bind(item: MessageItem) {
            binding.managerChatItemTxtMessage.text = item.message
        }
    }
}
