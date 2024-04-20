package com.changs.android.gnuting_android.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.data.model.ChatRoomUser
import com.changs.android.gnuting_android.data.model.InUser
import com.changs.android.gnuting_android.databinding.ChatRoomCurrentMemberItemBinding
import com.changs.android.gnuting_android.databinding.PostCurrentMemberItemBinding


class ChatRoomCurrentMemberAdapter(
    private val myUserId: Int?, private val navigateListener: (InUser) -> Unit
) : ListAdapter<ChatRoomUser, ChatRoomCurrentMemberAdapter.ViewHolder>(object :
    DiffUtil.ItemCallback<ChatRoomUser>() {
    override fun areItemsTheSame(oldItem: ChatRoomUser, newItem: ChatRoomUser): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: ChatRoomUser, newItem: ChatRoomUser): Boolean {
        return oldItem.id == newItem.id
    }
}) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent, myUserId, navigateListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    class ViewHolder(
        parent: ViewGroup,
        private val myUserId: Int?,
        private val navigateListener: (InUser) -> Unit
    ) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_room_current_member_item, parent, false)
    ) {
        private val binding = ChatRoomCurrentMemberItemBinding.bind(itemView)

        fun bind(item: ChatRoomUser) {
            binding.chatRoomCurrentMemberItemTxtName.text = item.nickname

            if (myUserId == item.userId) {
                binding.chatRoomCurrentMemberItemImgUserMe.isVisible = true
            } else {
                binding.chatRoomCurrentMemberItemImgUserMe.isVisible = false
            }

            Glide.with(binding.root.context).load(item.profileImage).error(R.drawable.ic_profile)
                .into(binding.chatRoomCurrentMemberItemImgProfile)

            binding.chatRoomCurrentMemberItemImgProfile.setOnClickListener {
                val inUser = InUser(
                    age = "",
                    department = "",
                    gender = "",
                    nickname = item.nickname,
                    id = item.userId,
                    profileImage = item.profileImage,
                    studentId = "",
                    userRole = "",
                    userSelfIntroduction = ""
                )
                navigateListener(inUser)
            }
        }
    }
}