package com.changs.android.gnuting_android.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
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

            val info = "${item.applyLeaderDepartment} | ${item.leaderUserDepartment}"

            binding.chatItemTxtTitle.text = item.title
            binding.chatItemTxtInfo.text = info

            binding.root.setOnClickListener {
                listener(item.id, item.title, info)
            }

            binding.chatItemImgProfile.isVisible = false
            binding.chatItemClProfileImgCount3Container.isVisible = false
            binding.chatItemClProfileImgCount4Container.isVisible = false

            when (item.chatRoomUserProfileImages.size) {
                1 -> {
                    Glide.with(binding.root.context).load(item.chatRoomUserProfileImages[0]).error(R.drawable.ic_profile)
                        .into(binding.chatItemImgProfile)

                    binding.chatItemImgProfile.isVisible = true
                }

                3 -> {
                    Glide.with(binding.root.context).load(item.chatRoomUserProfileImages[0]).error(R.drawable.ic_profile)
                        .into(binding.chatItemImgProfileImgTypeCount3Img1)

                    Glide.with(binding.root.context).load(item.chatRoomUserProfileImages[1]).error(R.drawable.ic_profile)
                        .into(binding.chatItemImgProfileImgTypeCount3Img2)

                    Glide.with(binding.root.context).load(item.chatRoomUserProfileImages[2]).error(R.drawable.ic_profile)
                        .into(binding.chatItemImgProfileImgTypeCount3Img3)

                    binding.chatItemClProfileImgCount3Container.isVisible = true
                }

                in 4..10 -> {
                    Glide.with(binding.root.context).load(item.chatRoomUserProfileImages[0]).error(R.drawable.ic_profile)
                        .into(binding.chatItemImgProfileImgTypeCount4Img1)

                    Glide.with(binding.root.context).load(item.chatRoomUserProfileImages[1]).error(R.drawable.ic_profile)
                        .into(binding.chatItemImgProfileImgTypeCount4Img2)

                    Glide.with(binding.root.context).load(item.chatRoomUserProfileImages[2]).error(R.drawable.ic_profile)
                        .into(binding.chatItemImgProfileImgTypeCount4Img3)

                    Glide.with(binding.root.context).load(item.chatRoomUserProfileImages[3]).error(R.drawable.ic_profile)
                        .into(binding.chatItemImgProfileImgTypeCount4Img4)

                    binding.chatItemClProfileImgCount4Container.isVisible = true
                }

                else -> {

                }

            }
        }
    }
}