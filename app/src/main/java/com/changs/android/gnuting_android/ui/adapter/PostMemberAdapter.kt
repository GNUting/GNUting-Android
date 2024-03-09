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
import com.changs.android.gnuting_android.data.model.InUser
import com.changs.android.gnuting_android.data.model.Member
import com.changs.android.gnuting_android.data.model.PostListItem
import com.changs.android.gnuting_android.databinding.HomeListItemBinding
import com.changs.android.gnuting_android.databinding.PostListItemBinding
import com.changs.android.gnuting_android.databinding.PostMemberItemBinding


class PostMemberAdapter :
    ListAdapter<InUser, PostMemberAdapter.ViewHolder>(object : DiffUtil.ItemCallback<InUser>() {
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
        LayoutInflater.from(parent.context).inflate(R.layout.post_member_item, parent, false)
    ) {
        private val binding = PostMemberItemBinding.bind(itemView)

        fun bind(item: InUser) {
            val info = "${item.department} | ${item.age} | ${item.studentId}"

            val text = binding.root.context.getString(R.string.post_member_txt).format(
                item.nickname, info
            )
            val styledText: Spanned = Html.fromHtml(text, FROM_HTML_MODE_LEGACY)

            binding.postMemberItemInfo.text = styledText
            binding.postMemberItemIntro.text = item.userSelfIntroduction
        }
    }
}