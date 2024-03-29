package com.changs.android.gnuting_android.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.data.model.InUser
import com.changs.android.gnuting_android.databinding.PostCurrentMemberItemBinding


class PostCurrentMemberAdapter(private val navigateListener: (InUser) -> Unit) :
    ListAdapter<InUser, PostCurrentMemberAdapter.ViewHolder>(object :
        DiffUtil.ItemCallback<InUser>() {
        override fun areItemsTheSame(oldItem: InUser, newItem: InUser): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: InUser, newItem: InUser): Boolean {
            return oldItem.id == newItem.id
        }
    }) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent, navigateListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    class ViewHolder(parent: ViewGroup, private val navigateListener: (InUser) -> Unit) :
        RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.post_current_member_item, parent, false)
        ) {
        private val binding = PostCurrentMemberItemBinding.bind(itemView)

        fun bind(item: InUser) {
            binding.postCurrentMemberTxtName.text = item.nickname
            val info = "${item.department} | ${item.studentId} | ${item.age}"

            Glide.with(binding.root.context).load(item.profileImage).error(R.drawable.ic_profile)
                .into(binding.postCurrentMemberItemImg)

            binding.postCurrentMemberItemImg.setOnClickListener {
                navigateListener(item)
            }

            binding.postCurrentMemberTxtMemberInfo.text = info
            binding.postCurrentMemberTxtMemberIntro.text = item.userSelfIntroduction
        }
    }
}