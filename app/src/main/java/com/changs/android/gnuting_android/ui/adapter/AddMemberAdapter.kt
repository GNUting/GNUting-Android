package com.changs.android.gnuting_android.ui.adapter

import android.content.Intent
import android.provider.Settings.Global.getString
import android.text.Html
import android.text.Html.FROM_HTML_MODE_LEGACY
import android.text.Spanned
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.data.model.HomePostItem
import com.changs.android.gnuting_android.data.model.InUser
import com.changs.android.gnuting_android.data.model.Member
import com.changs.android.gnuting_android.data.model.PostListItem
import com.changs.android.gnuting_android.databinding.AddMemberItemBinding
import com.changs.android.gnuting_android.databinding.HomeListItemBinding
import com.changs.android.gnuting_android.databinding.PostListItemBinding
import com.changs.android.gnuting_android.databinding.PostMemberItemBinding
import com.changs.android.gnuting_android.ui.PhotoActivity


class AddMemberAdapter(
    private val myUserId: Int?, private val listener: (InUser, Boolean) -> Unit
) : ListAdapter<InUser, AddMemberAdapter.ViewHolder>(object : DiffUtil.ItemCallback<InUser>() {
    override fun areItemsTheSame(oldItem: InUser, newItem: InUser): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: InUser, newItem: InUser): Boolean {
        return oldItem.id == newItem.id
    }
}) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent, myUserId, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    class ViewHolder(
        parent: ViewGroup,
        private val myUserId: Int?,
        private val listener: (InUser, Boolean) -> Unit
    ) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.add_member_item, parent, false)
    ) {
        private val binding = AddMemberItemBinding.bind(itemView)

        fun bind(item: InUser) {
            binding.addMemberItemCheckableLayout.isChecked = item.isChecked

            binding.addMemberItemCheckableLayout.setOnClickListener {
                if (myUserId != item.id) {
                    binding.addMemberItemCheckableLayout.isChecked =
                        !binding.addMemberItemCheckableLayout.isChecked
                    item.isChecked = binding.addMemberItemCheckableLayout.isChecked
                    listener(item, binding.addMemberItemCheckableLayout.isChecked)
                } else {
                    item.isChecked = true
                    binding.addMemberItemCheckableLayout.isChecked = true
                }
            }

            binding.postCurrentMemberTxtName.text = item.nickname

            Glide.with(binding.root.context).load(item.profileImage).error(R.drawable.ic_profile)
                .into(binding.postCurrentMemberItemImg)

            binding.postCurrentMemberItemImg.setOnClickListener {
                val intent = Intent(binding.root.context, PhotoActivity::class.java)
                intent.putExtra("img", item.profileImage)
                binding.root.context.startActivity(intent)
            }

            val info = "${item.department} | ${item.age} | ${item.studentId}"

            binding.addMemberItemInfo.text = info
            binding.addMemberItemIntro.text = item.userSelfIntroduction
        }
    }
}