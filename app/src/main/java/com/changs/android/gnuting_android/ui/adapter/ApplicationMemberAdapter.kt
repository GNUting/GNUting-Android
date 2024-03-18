package com.changs.android.gnuting_android.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.data.model.InUser
import com.changs.android.gnuting_android.databinding.ApplicationMemberItemBinding
import com.changs.android.gnuting_android.ui.PhotoActivity


class ApplicationMemberAdapter :
    ListAdapter<InUser, ApplicationMemberAdapter.ViewHolder>(object :
        DiffUtil.ItemCallback<InUser>() {
        override fun areItemsTheSame(oldItem: InUser, newItem: InUser): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: InUser, newItem: InUser): Boolean {
            return oldItem.id == newItem.id
        }
    }) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.application_member_item, parent, false)
    ) {
        private val binding = ApplicationMemberItemBinding.bind(itemView)

        fun bind(item: InUser) {
            binding.applicationMemberTxtName.text = item.nickname
            val info = "${item.studentId} | ${item.age}"

            Glide.with(binding.root.context).load(item.profileImage).error(R.drawable.ic_profile)
                .into(binding.applicationMemberItemImg)

            binding.applicationMemberItemImg.setOnClickListener {
                val intent = Intent(binding.root.context, PhotoActivity::class.java)
                intent.putExtra("img", item.profileImage)
                binding.root.context.startActivity(intent)
            }
            binding.applicationMemberTxtMemberInfo.text = info
            binding.applicationMemberTxtMemberIntro.text = item.userSelfIntroduction
        }
    }
}