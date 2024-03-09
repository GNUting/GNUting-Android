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
import com.changs.android.gnuting_android.data.model.ApplicationItem
import com.changs.android.gnuting_android.data.model.ApplicationResult
import com.changs.android.gnuting_android.data.model.HomePostItem
import com.changs.android.gnuting_android.data.model.Member
import com.changs.android.gnuting_android.data.model.PostListItem
import com.changs.android.gnuting_android.databinding.AddMemberItemBinding
import com.changs.android.gnuting_android.databinding.ApplicationListItemBinding
import com.changs.android.gnuting_android.databinding.HomeListItemBinding
import com.changs.android.gnuting_android.databinding.PostListItemBinding
import com.changs.android.gnuting_android.databinding.PostMemberItemBinding


class ApplicationAdapter(private val listener: (ApplicationResult) -> Unit) :
    ListAdapter<ApplicationResult, ApplicationAdapter.ViewHolder>(object : DiffUtil.ItemCallback<ApplicationResult>() {
        override fun areItemsTheSame(oldItem: ApplicationResult, newItem: ApplicationResult): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ApplicationResult, newItem: ApplicationResult): Boolean {
            return oldItem == newItem
        }
    }) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    class ViewHolder(parent: ViewGroup, val listener: (ApplicationResult) -> Unit) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.application_list_item, parent, false)
    ) {
        private val binding = ApplicationListItemBinding.bind(itemView)

        fun bind(item: ApplicationResult) {
            binding.root.setOnClickListener {
                listener(item)
            }

            binding.applicationListTxtMemberCount.text = "${item.applyUserCount}명"
            binding.applicationListTxtDepartment.text = item.applyUserDepartment

            when (item.applyStatus) {
                "대기중" -> {
                    binding.applicationListTxtStatus.text = "대기중"
                    binding.applicationListLlStatusContainer.setBackgroundResource(R.drawable.background_radius_10dp_solid_gray7)
                }
                "거절" -> {
                    binding.applicationListTxtStatus.text = "거절됨"
                    binding.applicationListLlStatusContainer.setBackgroundResource(R.drawable.background_radius_10dp_solid_main)
                }
                else -> {
                    binding.applicationListTxtStatus.text = "수락"
                    binding.applicationListLlStatusContainer.setBackgroundResource(R.drawable.background_radius_10dp_solid_secondary)
                }
            }
        }
    }
}