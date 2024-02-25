package com.changs.android.gnuting_android.ui.adapter

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
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.data.model.HomePostItem
import com.changs.android.gnuting_android.data.model.Member
import com.changs.android.gnuting_android.data.model.PostListItem
import com.changs.android.gnuting_android.databinding.AddMemberItemBinding
import com.changs.android.gnuting_android.databinding.HomeListItemBinding
import com.changs.android.gnuting_android.databinding.PostListItemBinding
import com.changs.android.gnuting_android.databinding.PostMemberItemBinding


class AddMemberAdapter :
    ListAdapter<Member, AddMemberAdapter.ViewHolder>(object : DiffUtil.ItemCallback<Member>() {
        override fun areItemsTheSame(oldItem: Member, newItem: Member): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Member, newItem: Member): Boolean {
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
        LayoutInflater.from(parent.context).inflate(R.layout.add_member_item, parent, false)
    ) {
        private val binding = AddMemberItemBinding.bind(itemView)

        init {
            binding.addMemberItemCheckableLayout.setOnClickListener {
                binding.addMemberItemCheckableLayout.isChecked = !binding.addMemberItemCheckableLayout.isChecked
            }
        }

        fun bind(item: Member) {
            val name = "${item.nickName}(${item.id})"
            val info = "${item.department} | ${item.age} | ${item.studentId}"

            val text = binding.root.context.getString(R.string.post_member_txt).format(
                name, info
            )
            val styledText: Spanned = Html.fromHtml(text, FROM_HTML_MODE_LEGACY)

            binding.addMemberItemInfo.text = styledText
            binding.addMemberItemIntro.text = item.intro
        }
    }
}