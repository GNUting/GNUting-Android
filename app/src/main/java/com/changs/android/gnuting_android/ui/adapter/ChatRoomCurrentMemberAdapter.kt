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
import com.changs.android.gnuting_android.data.model.ChatRoomUsersResult
import com.changs.android.gnuting_android.data.model.InUser
import com.changs.android.gnuting_android.databinding.ChatRoomCurrentMemberItemBinding


class ChatRoomCurrentMemberAdapter(
    private val myUserId: Int?, private val navigateListener: (InUser) -> Unit
) : ListAdapter<ChatRoomUsersResult, ChatRoomCurrentMemberAdapter.ViewHolder>(object :
    DiffUtil.ItemCallback<ChatRoomUsersResult>() {
    override fun areItemsTheSame(oldItem: ChatRoomUsersResult, newItem: ChatRoomUsersResult): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: ChatRoomUsersResult, newItem: ChatRoomUsersResult): Boolean {
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

        fun bind(item: ChatRoomUsersResult) {
            binding.chatRoomCurrentMemberItemTxtName.text = item.nickname

            if (myUserId == item.userId) {
                binding.chatRoomCurrentMemberItemImgUserMe.isVisible = true
            } else {
                binding.chatRoomCurrentMemberItemImgUserMe.isVisible = false
            }

            Glide.with(binding.root.context).load(item.profileImage)
                .error(R.drawable.ic_profile)
                .into(binding.chatRoomCurrentMemberItemImgProfile)

            binding.chatRoomCurrentMemberItemImgProfile.setOnClickListener {
                val inUser = InUser(
                    age = "",
                    department = item.department,
                    gender = "",
                    nickname = item.nickname,
                    id = item.userId,
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